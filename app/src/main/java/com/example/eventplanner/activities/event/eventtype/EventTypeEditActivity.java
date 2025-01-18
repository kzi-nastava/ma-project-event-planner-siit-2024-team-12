package com.example.eventplanner.activities.event.eventtype;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.eventplanner.R;

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


        Button categoriesButton = findViewById(R.id.recommendedCategoriesButton);

        String[] categories = {"Category 1", "Category 2", "Category 3", "Category 4"};
        boolean[] selectedCategories = new boolean[categories.length];

        categoriesButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select Categories");

            builder.setMultiChoiceItems(categories, selectedCategories, (dialog, which, isChecked) -> {
                selectedCategories[which] = isChecked;
            });

            builder.setPositiveButton("OK", (dialog, which) -> {
                StringBuilder selected = new StringBuilder();
                for (int i = 0; i < categories.length; i++) {
                    if (selectedCategories[i]) {
                        if (selected.length() > 0) selected.append(", ");
                        selected.append(categories[i]);
                    }
                }
                categoriesButton.setText(selected.length() > 0 ? selected.toString() : "Recommended categories");
            });

            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

            builder.create().show();
        });




    }


    public void closeForm(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }

}