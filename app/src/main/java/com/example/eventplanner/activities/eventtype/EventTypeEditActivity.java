package com.example.eventplanner.activities.eventtype;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
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
import com.example.eventplanner.activities.solutioncategory.SolutionCategoryService;
import com.example.eventplanner.dto.eventtype.UpdateEventTypeDTO;
import com.example.eventplanner.dto.solutioncategory.GetSolutionCategoryDTO;
import com.example.eventplanner.model.EventType;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventTypeEditActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_event_type_edit);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // receive data passed from EventTypeAdapter
        Intent intent = getIntent();

        String name = intent.getStringExtra("eventTypeName");
        String description = intent.getStringExtra("eventTypeDescription");
        ArrayList<String> selectedCategoryNames = intent.getStringArrayListExtra("suggestedCategoryNames");


        EditText nameText = findViewById(R.id.eventTypeName);
        EditText descriptionText = findViewById(R.id.eventTypeDescription);

        nameText.setText(name);
        // make the name field read-only
        nameText.setFocusable(false);
        nameText.setFocusableInTouchMode(false);
        nameText.setInputType(InputType.TYPE_NULL);


        descriptionText.setText(description);


        // set on click listener for categoriesButton
        Button categoriesButton = findViewById(R.id.recommendedCategoriesButton);

        categoriesButton.setOnClickListener(v -> {
            loadAcceptedCategories(categoriesButton, selectedCategoryNames);
        });


        // set on click listener for edit button
        Button editButton = findViewById(R.id.editButton);

        editButton.setOnClickListener(v -> {
            Log.d("EventTypeEdit", "Button text before processing: " + categoriesButton.getText());

            String[] categoriesFromButton = categoriesButton.getText().toString().split(", ");
            List<String> updatedSelectedCategories = new ArrayList<>();
            for (String category : categoriesFromButton) {
                if (!category.equals("Recommended categories")) {
                    updatedSelectedCategories.add(category.trim());
                }
            }

            Log.d("EventTypeEdit", "Updated categories: " + updatedSelectedCategories);

            selectedCategoryNames.clear();
            selectedCategoryNames.addAll(updatedSelectedCategories);

            String typeDescription = descriptionText.getText().toString();

            UpdateEventTypeDTO dto = new UpdateEventTypeDTO();
            dto.setDescription(typeDescription);
            Log.d("PROSLEDJENOO ", "Cat " + selectedCategoryNames);
            dto.setSuggestedCategoryNames(selectedCategoryNames);

            updateEventType(dto);
        });

    }


    private void loadAcceptedCategories(Button categoriesButton, ArrayList<String> selectedCategoryNames) {
        Call<List<GetSolutionCategoryDTO>> call = ClientUtils.solutionCategoryService.getAllAccepted();

        call.enqueue(new Callback<List<GetSolutionCategoryDTO>>() {
            @Override
            public void onResponse(Call<List<GetSolutionCategoryDTO>> call, Response<List<GetSolutionCategoryDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<GetSolutionCategoryDTO> categoryDTOs = response.body();
                    List<String> allCategoryNames = new ArrayList<>();
                    for (GetSolutionCategoryDTO dto : categoryDTOs) {
                        allCategoryNames.add(dto.getName());
                    }

                    if (!allCategoryNames.isEmpty()) {
                        showMultiChoiceDialog(allCategoryNames, selectedCategoryNames, categoriesButton);
                    } else {
                        Toast.makeText(EventTypeEditActivity.this, "No categories available.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(EventTypeEditActivity.this, "Failed to load categories.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<GetSolutionCategoryDTO>> call, Throwable t) {
                Toast.makeText(EventTypeEditActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void showMultiChoiceDialog(List<String> allCategories, List<String> selectedCategoryNames, Button categoriesButton) {
        String[] categoriesArray = allCategories.toArray(new String[0]);
        boolean[] selectedCategories = new boolean[categoriesArray.length];

        // preselect already suggested categories
        for (int i = 0; i < categoriesArray.length; i++) {
            selectedCategories[i] = selectedCategoryNames.contains(categoriesArray[i]);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Recommended categories");

        builder.setMultiChoiceItems(categoriesArray, selectedCategories, (dialog, which, isChecked) -> {
            selectedCategories[which] = isChecked;
        });

        builder.setPositiveButton("OK", (dialog, which) -> {
            // save new choice of suggested categories
            selectedCategoryNames.clear();
            for (int i = 0; i < categoriesArray.length; i++) {
                if (selectedCategories[i]) {
                    selectedCategoryNames.add(categoriesArray[i]);
                }
            }

            // display checked categories on the button
            StringBuilder selected = new StringBuilder();
            for (String category : selectedCategoryNames) {
                if (selected.length() > 0) selected.append(", ");
                selected.append(category);
            }
            categoriesButton.setText(selected.length() > 0 ? selected.toString() : "Recommended categories");
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }






    private void updateEventType(UpdateEventTypeDTO dto) {
        Intent intent = getIntent();
        String id = intent.getStringExtra("eventTypeId");

        Log.d("PARAMETRI ", "Params " + dto.getSuggestedCategoryNames());
        Call<EventType> call = ClientUtils.eventTypeService.updateEventType(dto, Long.parseLong(id));

        call.enqueue(new Callback<EventType>() {
            @Override
            public void onResponse(Call<EventType> call, Response<EventType> response) {
                if (response.isSuccessful()) {
                    EventType updatedEventType = response.body();

                    runOnUiThread(() -> {
                        Toast.makeText(EventTypeEditActivity.this, "Event type updated successfully!", Toast.LENGTH_SHORT).show();
                    });

                    Intent intent = new Intent(EventTypeEditActivity.this, EventTypeTableActivity.class);
                    startActivity(intent);
                }

                else if (response.code() == 103) {
                    Toast.makeText(EventTypeEditActivity.this, "103", Toast.LENGTH_SHORT).show();
                }

                else {
                    runOnUiThread(() -> {
                        Toast.makeText(EventTypeEditActivity.this, "Failed to update: " + response.message(), Toast.LENGTH_SHORT).show();
                    });
                }
            }

            @Override
            public void onFailure(Call<EventType> call, Throwable t) {;
                runOnUiThread(() -> {
                    Toast.makeText(EventTypeEditActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }



    public void closeForm(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }

}