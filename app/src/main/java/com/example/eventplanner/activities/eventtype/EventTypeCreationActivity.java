package com.example.eventplanner.activities.eventtype;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.eventplanner.ClientUtils;
import com.example.eventplanner.R;
import com.example.eventplanner.ValidationUtils;
import com.example.eventplanner.dto.eventtype.CreateEventTypeDTO;
import com.example.eventplanner.dto.solutioncategory.GetSolutionCategoryDTO;
import com.example.eventplanner.model.EventType;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventTypeCreationActivity extends AppCompatActivity {

    private boolean[] selectedCategories;
    private String[] categories;
    private List<String> selectedCategoryNames = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_event_type_creation);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        Button categoriesButton = findViewById(R.id.recommendedCategoriesButton);
        loadCategories(categoriesButton);

        Button createButton = findViewById(R.id.createButton);
        createButton.setOnClickListener(v -> {
            createCategory();
        });

    }



    private void loadCategories(Button categoriesButton) {
        Call<List<GetSolutionCategoryDTO>> call = ClientUtils.solutionCategoryService.getAllAccepted();

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
                        AlertDialog.Builder builder = new AlertDialog.Builder(EventTypeCreationActivity.this);
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
                    Toast.makeText(EventTypeCreationActivity.this, "Failed to load categories", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<GetSolutionCategoryDTO>> call, Throwable t) {
                Toast.makeText(EventTypeCreationActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void createCategory() {
        EditText nameText = findViewById(R.id.name);
        EditText descriptionText = findViewById(R.id.description);

        // validate input data
        if (!ValidationUtils.isFieldValid(nameText, "Name is required!")) return;
        if (!ValidationUtils.isFieldValid(descriptionText, "Description is required!")) return;

        // admin doesn't have to select suggested categories when creating event type
        // in case no appropriate categories are available in that moment
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

        Call<EventType> call = ClientUtils.eventTypeService.createEventType(createEventTypeDTO);

        call.enqueue(new Callback<EventType>() {
            @Override
            public void onResponse(Call<EventType> call, Response<EventType> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EventTypeCreationActivity.this, "Successfully created event type!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(EventTypeCreationActivity.this, EventTypeTableActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<EventType> call, Throwable t) {
                Toast.makeText(EventTypeCreationActivity.this, "Error creating event type!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void closeForm(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }

}