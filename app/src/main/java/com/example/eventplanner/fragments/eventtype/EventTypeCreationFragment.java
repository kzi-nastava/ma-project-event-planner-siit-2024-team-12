package com.example.eventplanner.fragments.eventtype;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.eventplanner.utils.ClientUtils;
import com.example.eventplanner.R;
import com.example.eventplanner.utils.ValidationUtils;
import com.example.eventplanner.dto.eventtype.CreateEventTypeDTO;
import com.example.eventplanner.dto.eventtype.GetEventTypeDTO;
import com.example.eventplanner.dto.solutioncategory.GetSolutionCategoryDTO;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventTypeCreationFragment extends Fragment {

    private boolean[] selectedCategories;
    private String[] categories;
    private List<String> selectedCategoryNames = new ArrayList<>();
    private View view;



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_event_type_creation, container, false);

        Button categoriesButton = view.findViewById(R.id.recommendedCategoriesButton);
        loadCategories(categoriesButton);

        Button createButton = view.findViewById(R.id.createButton);
        createButton.setOnClickListener(v -> {
            createCategory();
        });

        return view;
    }



    private void loadCategories(Button categoriesButton) {
        String auth = ClientUtils.getAuthorization(requireContext());

        Call<List<GetSolutionCategoryDTO>> call = ClientUtils.solutionCategoryService.getAllAccepted(auth);

        call.enqueue(new Callback<List<GetSolutionCategoryDTO>>() {
            @Override
            public void onResponse(Call<List<GetSolutionCategoryDTO>> call, Response<List<GetSolutionCategoryDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<GetSolutionCategoryDTO> categoriesList = response.body();
                    categories = new String[categoriesList.size()];
                    selectedCategories = new boolean[categoriesList.size()];

                    for (int i = 0; i < categoriesList.size(); i++) {
                        categories[i] = categoriesList.get(i).getName();
                    }

                    categoriesButton.setOnClickListener(v -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                        builder.setTitle("Select Categories");

                        builder.setMultiChoiceItems(categories, selectedCategories, (dialog, which, isChecked) -> {
                            selectedCategories[which] = isChecked;
                        });

                        builder.setPositiveButton("OK", (dialog, which) -> {
                            selectedCategoryNames.clear();
                            StringBuilder selected = new StringBuilder();
                            for (int i = 0; i < categories.length; i++) {
                                if (selectedCategories[i]) {
                                    selectedCategoryNames.add(categories[i]);
                                    if (selected.length() > 0) selected.append(", ");
                                    selected.append(categories[i]);
                                }
                            }
                            categoriesButton.setText(selected.length() > 0 ? selected.toString() : "Recommended categories");
                        });

                        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

                        builder.create().show();
                    });
                } else {
                    Toast.makeText(requireActivity(), "Failed to load categories", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<GetSolutionCategoryDTO>> call, Throwable t) {
                Toast.makeText(requireActivity(), "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void createCategory() {
        EditText nameText = view.findViewById(R.id.name);
        EditText descriptionText = view.findViewById(R.id.description);

        // validate input data
        if (!ValidationUtils.isFieldValid(nameText, "Name is required!")) return;
        if (!ValidationUtils.isFieldValid(descriptionText, "Description is required!")) return;

        // admin doesn't have to select suggested categories when creating event type
        // in case no appropriate categories are available at that moment
        // categories can always be added in event type edit
        /*
        if (selectedCategoryNames.isEmpty()) {
            Toast.makeText(this, "Select suggested categories!", Toast.LENGTH_SHORT).show();
            return;
        }
         */

        // if valid, save
        String name = nameText.getText().toString();
        String description = descriptionText.getText().toString();

        CreateEventTypeDTO createEventTypeDTO = new CreateEventTypeDTO();
        createEventTypeDTO.setName(name);
        createEventTypeDTO.setDescription(description);
        createEventTypeDTO.setCategoryNames(selectedCategoryNames);

        String auth = ClientUtils.getAuthorization(requireContext());

        Call<GetEventTypeDTO> call = ClientUtils.eventTypeService.createEventType(auth, createEventTypeDTO);

        call.enqueue(new Callback<GetEventTypeDTO>() {
            @Override
            public void onResponse(Call<GetEventTypeDTO> call, Response<GetEventTypeDTO> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(requireActivity(), "Successfully created event type!", Toast.LENGTH_SHORT).show();

                    requireActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.main_fragment_container, new EventTypeTableFragment())
                            .commit();
                }
            }

            @Override
            public void onFailure(Call<GetEventTypeDTO> call, Throwable t) {
                Toast.makeText(requireActivity(), "Error creating event type!", Toast.LENGTH_SHORT).show();
            }
        });
    }

}