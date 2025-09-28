package com.example.eventplanner.fragments.product;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.eventplanner.R;
import com.example.eventplanner.dto.eventtype.GetEventTypeDTO;
import com.example.eventplanner.dto.solutioncategory.GetSolutionCategoryDTO;
import com.example.eventplanner.utils.ClientUtils;
import com.example.eventplanner.utils.ValidationUtils;
import com.example.eventplanner.viewmodels.ProductCreationViewModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProductCreationFragment extends DialogFragment {
    private View view;
    private boolean[] selectedEventTypes;
    private String[] eventTypes;
    private List<String> selectedEventTypeNames = new ArrayList<>();
    private Button eventTypesBtn;
    private Spinner categorySpinner;
    private List<String> categoryNames = new ArrayList<>();
    private String categoryTxt;
    private ProductCreationViewModel viewModel;
    private EditText nameField, descriptionField;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_product_creation, container, false);

        viewModel = new ViewModelProvider(requireActivity()).get(ProductCreationViewModel.class);

        eventTypesBtn = view.findViewById(R.id.event_types_btn);
        eventTypesBtn.setOnClickListener(v -> {
            loadEventTypes();
        });

        loadCategories();

        ImageView addCategory = view.findViewById(R.id.add_category);
        addCategory.setOnClickListener(v -> {
            CategoryRecommendationFragment fragment = new CategoryRecommendationFragment();
            fragment.show(getParentFragmentManager(), "CategoryRecommendation");
        });


        Button nextBtn = view.findViewById(R.id.nextBtn);
        nextBtn.setOnClickListener(v -> {
            saveFirstForm();
        });

        return view;
    }


    private void loadCategories() {
        String auth = ClientUtils.getAuthorization(requireContext());

        Call<List<GetSolutionCategoryDTO>> call = ClientUtils.solutionCategoryService.getAllAccepted(auth);
        call.enqueue(new Callback<List<GetSolutionCategoryDTO>>() {
            @Override
            public void onResponse(Call<List<GetSolutionCategoryDTO>> call, Response<List<GetSolutionCategoryDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<GetSolutionCategoryDTO> categoryDTOS = response.body();
                    categoryNames.clear();

                    for (GetSolutionCategoryDTO dto : categoryDTOS) {
                        categoryNames.add(dto.getName());
                    }

                    setUpCategorySpinner();
                }
            }

            @Override
            public void onFailure(Call<List<GetSolutionCategoryDTO>> call, Throwable t) {
                Toast.makeText(getActivity(), "Failed to load solution categories", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void setUpCategorySpinner() {
        categorySpinner = view.findViewById(R.id.categorySpinner);

        if (categoryNames.isEmpty()) {
            return;
        }

        categoryNames.add(0, "");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_item, categoryNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        categorySpinner.setSelection(0);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (position == 0) {
                    categoryTxt = null;
                } else {
                    categoryTxt = categoryNames.get(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });
    }



    private void buildAlertDialog(String[] eventTypes, boolean[] selectedEventTypes) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Select event types :");

        builder.setMultiChoiceItems(eventTypes, selectedEventTypes, (dialog, which, isChecked) -> {
            selectedEventTypes[which] = isChecked;
        });

        builder.setPositiveButton("OK", (dialog, which) -> {
            selectedEventTypeNames.clear();
            StringBuilder selected = new StringBuilder();
            for (int i = 0; i < eventTypes.length; i++) {
                if (selectedEventTypes[i]) {
                    selectedEventTypeNames.add(eventTypes[i]);
                    if (selected.length() > 0) selected.append(", ");
                    selected.append(eventTypes[i]);
                }
            }
            eventTypesBtn.setText(selected.length() > 0 ? selected.toString() : "Select event types");
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }


    private void loadEventTypes() {
        String auth = ClientUtils.getAuthorization(requireContext());

        Call<ArrayList<GetEventTypeDTO>> call = ClientUtils.eventTypeService.getAllActive(auth);
        call.enqueue(new Callback<ArrayList<GetEventTypeDTO>>() {
            @Override
            public void onResponse(Call<ArrayList<GetEventTypeDTO>> call, Response<ArrayList<GetEventTypeDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<GetEventTypeDTO> eventTypeDTOS = response.body();
                    String[] newEventTypes = new String[eventTypeDTOS.size()];
                    boolean[] newSelectedEventTypes = new boolean[eventTypeDTOS.size()];

                    for (int i = 0; i < eventTypeDTOS.size(); i++) {
                        newEventTypes[i] = eventTypeDTOS.get(i).getName();

                        if (selectedEventTypeNames.contains(newEventTypes[i])) {
                            newSelectedEventTypes[i] = true;
                        }
                    }

                    eventTypes = newEventTypes;
                    selectedEventTypes = newSelectedEventTypes;


                    buildAlertDialog(eventTypes, selectedEventTypes);

                }
            }

            @Override
            public void onFailure(Call<ArrayList<GetEventTypeDTO>> call, Throwable t) {

            }
        });
    }


    private void saveFirstForm() {
        nameField = view.findViewById(R.id.name);
        descriptionField = view.findViewById(R.id.description);

        if (!ValidationUtils.isFieldValid(nameField, "Name is required!")) return;
        if (!ValidationUtils.isFieldValid(descriptionField, "Description is required!")) return;

        viewModel.updateAttributes("name", nameField.getText().toString());
        viewModel.updateAttributes("description", descriptionField.getText().toString());
        viewModel.updateEventTypes(selectedEventTypeNames);

        if (!viewModel.isEventTypeSet) {
            Toast.makeText(getActivity(), "Set event types!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!viewModel.usedRecommendation) {
            if (categoryTxt != null) {
                viewModel.updateAttributes("category", categoryTxt);
            }
            else {
                Toast.makeText(getActivity(), "Set category!", Toast.LENGTH_SHORT).show();
                return;
            }
        }


        ProductCreationFragment2 fragment2 = new ProductCreationFragment2();
        fragment2.show(getParentFragmentManager(), "ProductCreation2");
    }

}