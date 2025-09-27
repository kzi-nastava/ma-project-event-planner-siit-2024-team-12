package com.example.eventplanner.activities.eventtype;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.eventplanner.R;

import java.util.ArrayList;

public class EventTypeViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_event_type_view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        // receive data passed from EventTypeAdapter
        Intent intent = getIntent();
        String name = intent.getStringExtra("eventTypeName");
        String description = intent.getStringExtra("eventTypeDescription");
        ArrayList<String> categories = intent.getStringArrayListExtra("suggestedCategoryNames");


        TextView nameText = findViewById(R.id.eventTypeName);
        TextView descriptionText = findViewById(R.id.eventTypeDescription);

        nameText.setText(name);
        descriptionText.setText(description);


        Button categoriesButton = findViewById(R.id.recommendedCategoriesButton);

        String[] categoriesArray = new String[categories.size()];
        categories.toArray(categoriesArray);

        boolean[] selectedCategories = new boolean[categoriesArray.length];

        categoriesButton.setOnClickListener(v -> {
            // select all suggested categories
            for (int i = 0; i < selectedCategories.length; i++) {
                selectedCategories[i] = true;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Recommended categories");


            builder.setMultiChoiceItems(categoriesArray, selectedCategories, (dialog, which, isChecked) -> {
                // do nothing because checkboxes should be disabled
            });


            builder.setPositiveButton("OK", (dialog, which) -> {
                StringBuilder selected = new StringBuilder();
                for (int i = 0; i < categoriesArray.length; i++) {
                    if (selectedCategories[i]) {
                        if (selected.length() > 0) selected.append(", ");
                        selected.append(categoriesArray[i]);
                    }
                }
                categoriesButton.setText(selected.length() > 0 ? selected.toString() : "Recommended categories");
            });


            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());


            AlertDialog dialog = builder.create();


            // disable interaction with checkboxes
            ListView listView = dialog.getListView();
            listView.setEnabled(false);

            dialog.show();
        });

    }

}