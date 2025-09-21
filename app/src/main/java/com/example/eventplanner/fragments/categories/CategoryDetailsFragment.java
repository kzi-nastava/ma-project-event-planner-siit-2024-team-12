package com.example.eventplanner.fragments.categories;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import com.example.eventplanner.R;
import com.example.eventplanner.dto.solutioncategory.GetCategoryDTO;

import java.io.Serializable;

public class CategoryDetailsFragment extends DialogFragment {

    private static final String ARG_CATEGORY = "category";
    private static final String ARG_IS_ACTIVE = "is_active";

    private GetCategoryDTO category;
    private boolean isActive;

    public static CategoryDetailsFragment newInstance(GetCategoryDTO category, boolean isActive) {
        CategoryDetailsFragment fragment = new CategoryDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_CATEGORY, (Serializable) category);
        args.putBoolean(ARG_IS_ACTIVE, isActive);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            category = (GetCategoryDTO) getArguments().getSerializable(ARG_CATEGORY);
            isActive = getArguments().getBoolean(ARG_IS_ACTIVE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category_details, container, false);

        TextView nameTextView = view.findViewById(R.id.textViewCategoryDetailsName);
        TextView descriptionTextView = view.findViewById(R.id.textViewCategoryDetailsDescription);
        Button deleteButton = view.findViewById(R.id.buttonDeleteCategory);
        Button actionButton = view.findViewById(R.id.buttonActionCategory);

        if (category != null) {
            nameTextView.setText(category.getName());
            descriptionTextView.setText(category.getDescription());
        }

        if (isActive) {
            actionButton.setText("Sačuvaj izmene");
            // Implementiraj logiku za dugme za izmenu
            actionButton.setOnClickListener(v -> {
                // Pozovi metodu za izmenu kategorije
            });
        } else {
            actionButton.setText("Odobri kategoriju");
            // Implementiraj logiku za dugme za odobravanje
            actionButton.setOnClickListener(v -> {
                // Pozovi metodu za odobravanje kategorije
            });
        }

        deleteButton.setOnClickListener(v -> {
            // Implementiraj logiku za brisanje kategorije
        });

        return view;
    }
}