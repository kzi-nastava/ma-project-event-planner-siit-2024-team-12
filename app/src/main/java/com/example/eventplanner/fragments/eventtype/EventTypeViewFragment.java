package com.example.eventplanner.fragments.eventtype;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.eventplanner.R;

import java.util.ArrayList;

public class EventTypeViewFragment extends DialogFragment {

    private View view;
    private String name, description;
    private ArrayList<String> categories;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_event_type_view, container, false);


        // receive data passed from EventTypeAdapter
        if (getArguments() != null) {
            name = getArguments().getString("eventTypeName");
            description = getArguments().getString("eventTypeDescription");
            categories = getArguments().getStringArrayList("suggestedCategoryNames");
        }


        TextView nameText = view.findViewById(R.id.eventTypeName);
        TextView descriptionText = view.findViewById(R.id.eventTypeDescription);

        nameText.setText(name);
        descriptionText.setText(description);


        Button categoriesButton = view.findViewById(R.id.recommendedCategoriesButton);

        String[] categoriesArray = new String[categories.size()];
        categories.toArray(categoriesArray);

        boolean[] selectedCategories = new boolean[categoriesArray.length];

        categoriesButton.setOnClickListener(v -> {
            // select all suggested categories
            for (int i = 0; i < selectedCategories.length; i++) {
                selectedCategories[i] = true;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
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

        return view;

    }
}