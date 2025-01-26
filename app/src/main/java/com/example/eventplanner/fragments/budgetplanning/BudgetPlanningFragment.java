package com.example.eventplanner.fragments.budgetplanning;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.eventplanner.ClientUtils;
import com.example.eventplanner.R;
import com.example.eventplanner.viewmodels.EventCreationViewModel;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class BudgetPlanningFragment extends Fragment {

    EventCreationViewModel viewModel;
    Spinner typeSpinner;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_budget_planning, container, false);

        viewModel = new ViewModelProvider(requireActivity()).get(EventCreationViewModel.class);

        typeSpinner = view.findViewById(R.id.eventTypeSpinner);

        List<String> eventTypes = loadEventTypes();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, eventTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(adapter);


        Button createBtn = view.findViewById(R.id.createBtn);
        createBtn.setOnClickListener(v -> {
            createEvent();
        });


        return view;
    }

    private List<String> loadEventTypes() {
        List<String> eventTypes = new ArrayList<>();
        eventTypes.add("Birthday");
        eventTypes.add("Wedding");
        eventTypes.add("Conference");
        eventTypes.add("Concert");
        return eventTypes;
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
                    Toast.makeText(getActivity(), "Success!", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getActivity(), "Noo!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getActivity(), t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}