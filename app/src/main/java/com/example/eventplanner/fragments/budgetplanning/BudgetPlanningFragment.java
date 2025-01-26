package com.example.eventplanner.fragments.budgetplanning;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.eventplanner.ClientUtils;
import com.example.eventplanner.R;
import com.example.eventplanner.model.EventType;
import com.example.eventplanner.viewmodels.EventCreationViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class BudgetPlanningFragment extends Fragment {

    EventCreationViewModel viewModel;
    Spinner typeSpinner;
    ArrayList<String> categories = new ArrayList<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_budget_planning, container, false);

        viewModel = new ViewModelProvider(requireActivity()).get(EventCreationViewModel.class);

        typeSpinner = view.findViewById(R.id.eventTypeSpinner);

        List<String> eventTypes = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, eventTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(adapter);

        loadEventTypes(adapter, eventTypes);

        Button createBtn = view.findViewById(R.id.createBtn);
        createBtn.setOnClickListener(v -> {
            createEvent();
        });


        Button categoriesButton = view.findViewById(R.id.recommendedCategoriesButton);

        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedEventType = (String) parent.getItemAtPosition(position);
                loadRecommendedCategories(selectedEventType, categoriesButton);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });


        return view;
    }


    private void loadEventTypes(ArrayAdapter<String> adapter, List<String> eventTypes) {
        Call<ArrayList<EventType>> call = ClientUtils.eventTypeService.getAllActive();

        call.enqueue(new Callback<ArrayList<EventType>>() {
            @Override
            public void onResponse(Call<ArrayList<EventType>> call, Response<ArrayList<EventType>> response) {
                if (response.isSuccessful()) {
                    ArrayList<EventType> types = response.body();
                    for (EventType eventType : types) {
                        eventTypes.add(eventType.getName());
                    }

                    Collections.sort(eventTypes);
                    adapter.notifyDataSetChanged();

                    if (!eventTypes.isEmpty()) {
                        typeSpinner.setSelection(0);
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<EventType>> call, Throwable t) {
                Toast.makeText(getActivity(), "Failed to load event types!", Toast.LENGTH_SHORT).show();
            }
        });
    }





    private void createEvent() {
        String eventType = typeSpinner.getSelectedItem().toString();

        viewModel.updateEventAttributes("eventType", eventType);
        viewModel.updateEventAttributes("organizer", "organizer3@example.com");

        Call<ResponseBody> call = ClientUtils.eventService.createEvent(viewModel.getDto().getValue());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getActivity(), "Successfully created event!", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getActivity(), "Failed to create event!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getActivity(), t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void loadRecommendedCategories(String eventType, Button categoriesButton) {
        Call<ArrayList<String>> call = ClientUtils.eventTypeService.getSuggestedCategories(eventType);

        call.enqueue(new Callback<ArrayList<String>>() {
            @Override
            public void onResponse(Call<ArrayList<String>> call, Response<ArrayList<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categories.clear();
                    categories.addAll(response.body());
                } else {
                    categories.clear();
                }

                // display suggested categories in the button
                StringBuilder categoriesText = new StringBuilder();
                for (String category : categories) {
                    if (categoriesText.length() > 0) categoriesText.append(", ");
                    categoriesText.append(category);
                }
                categoriesButton.setText(categoriesText.length() > 0 ? categoriesText.toString() : "No suggested categories!");


                categoriesButton.setOnClickListener(v -> {
                    String[] categoriesArray = categories.toArray(new String[0]);
                    boolean[] selectedCategories = new boolean[categoriesArray.length];

                    for (int i = 0; i < selectedCategories.length; i++) {
                        selectedCategories[i] = true;
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                    builder.setTitle("Suggested categories :");
                    builder.setMultiChoiceItems(categoriesArray, selectedCategories, (dialog, which, isChecked) -> { });

                    builder.setPositiveButton("OK", (dialog, which) -> {
                        StringBuilder selected = new StringBuilder();
                        for (int i = 0; i < categoriesArray.length; i++) {
                            if (selectedCategories[i]) {
                                if (selected.length() > 0) selected.append(", ");
                                selected.append(categoriesArray[i]);
                            }
                        }
                        categoriesButton.setText(selected.length() > 0 ? selected.toString() : "No suggested categories!");
                    });

                    builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

                    AlertDialog dialog = builder.create();

                    ListView listView = dialog.getListView();
                    listView.setEnabled(false);

                    dialog.show();

                });
            }

            @Override
            public void onFailure(Call<ArrayList<String>> call, Throwable t) {
                Toast.makeText(getActivity(), "Failed to load categories", Toast.LENGTH_SHORT).show();
            }
        });
    }

}