package com.example.eventplanner.activities.eventtype;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
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

import com.example.eventplanner.utils.ClientUtils;
import com.example.eventplanner.R;
import com.example.eventplanner.enumeration.UserRole;
import com.example.eventplanner.dto.eventtype.UpdateEventTypeDTO;
import com.example.eventplanner.dto.eventtype.GetEventTypeDTO;
import com.example.eventplanner.dto.solutioncategory.GetSolutionCategoryDTO;
import com.example.eventplanner.utils.ValidationUtils;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventTypeEditActivity extends AppCompatActivity {
    private Long currentId;
    private String name, description;
    private EditText nameField, descriptionField;
    private ArrayList<String> selectedCategoryNames;
    private Intent intent;
    private Button categoriesButton, deactivationButton, editButton;


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

        retrieveInitialValues();
        setFields();
        setUpCategoriesButton();
        setUpDeactivationButton();
        setUpEditButton();

    }


    private void setUpEditButton() {
        // set on click listener for edit button
        editButton = findViewById(R.id.editButton);
        categoriesButton.setText(selectedCategoryNames.isEmpty() ?
                getString(R.string.recommended_categories) :
                String.join(", ", selectedCategoryNames));

        editButton.setOnClickListener(v -> {
            String typeDescription = descriptionField.getText().toString();

            UpdateEventTypeDTO dto = new UpdateEventTypeDTO();
            dto.setDescription(typeDescription);
            dto.setSuggestedCategoryNames(selectedCategoryNames);

            updateEventType(dto);
        });
    }


    private void setUpDeactivationButton() {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String role = prefs.getString("userRole", UserRole.ROLE_ADMIN.toString());

        deactivationButton = findViewById(R.id.deactivateButton);

        // provider cannot (de)activate event type but can edit
        if (role.equals(UserRole.ROLE_PROVIDER.toString())) {
            deactivationButton.setVisibility(View.GONE);
        }

        boolean isActive = intent.getBooleanExtra("isActive", true);

        deactivationButton.setText(isActive ? getString(R.string.deactivate) : getString(R.string.activate));

        deactivationButton.setOnClickListener(v -> {
            confirmDeactivation(isActive);
        });

    }


    private void confirmDeactivation(boolean isActive) {
        // confirmation dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(EventTypeEditActivity.this);
        builder.setTitle(isActive ? getString(R.string.deactivate) + " " +
                EventTypeEditActivity.this.nameField.getText() + "?" :
                getString(R.string.activate) + " " + EventTypeEditActivity.this.nameField.getText() + "?");

        builder.setMessage(isActive ? getString(R.string.deactivation_confirmation) :
                getString(R.string.activation_confirmation));

        builder.setPositiveButton(getString(R.string.yes), (dialog, which) -> {
            if (isActive) {
                deactivateEventType(currentId, deactivationButton);
            } else {
                activateEventType(currentId, deactivationButton);
            }
        });

        builder.setNegativeButton(getString(R.string.no), (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void setUpCategoriesButton() {
        // set on click listener for categoriesButton
        categoriesButton = findViewById(R.id.recommendedCategoriesButton);
        categoriesButton.setOnClickListener(v -> {
            loadAcceptedCategories(categoriesButton, selectedCategoryNames);
        });
    }


    private void setFields() {
        nameField = findViewById(R.id.eventTypeName);
        descriptionField = findViewById(R.id.eventTypeDescription);

        // make the name field read-only
        nameField.setText(name);
        // could've just used TextView, but wanted to try this out
        nameField.setFocusable(false);
        nameField.setFocusableInTouchMode(false);
        nameField.setInputType(InputType.TYPE_NULL);

        descriptionField.setText(description);
    }


    private void retrieveInitialValues() {
        // receive data passed from EventTypeAdapter
        intent = getIntent();

        currentId = intent.getLongExtra("eventTypeId", 0);
        name = intent.getStringExtra("eventTypeName");
        description = intent.getStringExtra("eventTypeDescription");
        selectedCategoryNames = intent.getStringArrayListExtra("suggestedCategoryNames");
    }


    private void activateEventType(Long id, Button activationButton) {
        String auth = ClientUtils.getAuthorization(this);

        Call<ResponseBody> call = ClientUtils.eventTypeService.activateEventType(auth, id);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        Toast.makeText(EventTypeEditActivity.this, getString(R.string.activated_event_type), Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(EventTypeEditActivity.this, EventTypeTableActivity.class);
                        startActivity(intent);

                        activationButton.setText(getString(R.string.deactivate));
                    });
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(EventTypeEditActivity.this, getString(R.string.failed_to_activate) + response.message(), Toast.LENGTH_SHORT).show();
                    });
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                runOnUiThread(() -> {
                    Toast.makeText(EventTypeEditActivity.this, getString(R.string.error) + t.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }


    private void deactivateEventType(Long id, Button activationButton) {
        String auth = ClientUtils.getAuthorization(this);

        Call<ResponseBody> call = ClientUtils.eventTypeService.deactivateEventType(auth, id);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        Toast.makeText(EventTypeEditActivity.this, getString(R.string.deactivated_event_type), Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(EventTypeEditActivity.this, EventTypeTableActivity.class);
                        startActivity(intent);

                        activationButton.setText(getString(R.string.activate));

                    });
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(EventTypeEditActivity.this, getString(R.string.failed_to_deactivate) + response.message(), Toast.LENGTH_SHORT).show();
                    });
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                runOnUiThread(() -> {
                    Toast.makeText(EventTypeEditActivity.this, getString(R.string.error) + t.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }


    private void loadAcceptedCategories(Button categoriesButton, ArrayList<String> selectedCategoryNames) {
        String auth = ClientUtils.getAuthorization(this);

        Call<List<GetSolutionCategoryDTO>> call = ClientUtils.solutionCategoryService.getAllAccepted(auth);

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
                        Toast.makeText(EventTypeEditActivity.this, getString(R.string.no_categories_available), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(EventTypeEditActivity.this, getString(R.string.failed_to_load_categories), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<GetSolutionCategoryDTO>> call, Throwable t) {
                Toast.makeText(EventTypeEditActivity.this, getString(R.string.error) + t.getMessage(), Toast.LENGTH_SHORT).show();
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
        builder.setTitle(getString(R.string.recommended_categories));

        builder.setMultiChoiceItems(categoriesArray, selectedCategories, (dialog, which, isChecked) -> {
            selectedCategories[which] = isChecked;
        });

        builder.setPositiveButton(getString(R.string.ok), (dialog, which) -> {
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
            categoriesButton.setText(selected.length() > 0 ? selected.toString() : getString(R.string.recommended_categories));
        });

        builder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void updateEventType(UpdateEventTypeDTO dto) {
        String auth = ClientUtils.getAuthorization(this);

        if (!ValidationUtils.isFieldValid(descriptionField, "Description is required!")) return;
        // admin doesn't have to select suggested categories when updating event type
        // in case no appropriate categories are available at that moment

        Call<GetEventTypeDTO> call = ClientUtils.eventTypeService.updateEventType(auth, dto, currentId);

        call.enqueue(new Callback<GetEventTypeDTO>() {
            @Override
            public void onResponse(Call<GetEventTypeDTO> call, Response<GetEventTypeDTO> response) {
                if (response.isSuccessful()) {
                    GetEventTypeDTO updatedEventType = response.body();

                    runOnUiThread(() -> {
                        Toast.makeText(EventTypeEditActivity.this, getString(R.string.event_type_updated), Toast.LENGTH_SHORT).show();
                    });

                    Intent intent = new Intent(EventTypeEditActivity.this, EventTypeTableActivity.class);
                    startActivity(intent);
                }

                else if (response.code() == 103) {
                   // Toast.makeText(EventTypeEditActivity.this, "103", Toast.LENGTH_SHORT).show();
                }

                else {
                    runOnUiThread(() -> {
                        Toast.makeText(EventTypeEditActivity.this, getString(R.string.failed_to_update) + response.message(), Toast.LENGTH_SHORT).show();
                    });
                }
            }

            @Override
            public void onFailure(Call<GetEventTypeDTO> call, Throwable t) {;
                runOnUiThread(() -> {
                    //Toast.makeText(EventTypeEditActivity.this, getString(R.string.error) + t.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }


    public void closeForm(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }

}