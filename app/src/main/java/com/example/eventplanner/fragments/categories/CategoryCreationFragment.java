package com.example.eventplanner.fragments.categories;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.eventplanner.R;
import com.example.eventplanner.viewmodels.CategoryViewModel;

public class CategoryCreationFragment extends DialogFragment {

    private static final String ARG_CREATION_TYPE = "creation_type";
    private static final String TYPE_CREATE = "CREATE";
    private static final String TYPE_SUGGEST = "SUGGEST";
    private CategoryViewModel viewModel;

    private String creationType;

    private EditText nameEditText;
    private EditText descriptionEditText;

    public static CategoryCreationFragment newInstance(String creationType) {
        CategoryCreationFragment fragment = new CategoryCreationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CREATION_TYPE, creationType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            creationType = getArguments().getString(ARG_CREATION_TYPE);
        }
        viewModel = new ViewModelProvider(requireActivity()).get(CategoryViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category_creation, container, false);

        nameEditText = view.findViewById(R.id.editTextCategoryName);
        descriptionEditText = view.findViewById(R.id.editTextCategoryDesc);
        Button submitButton = view.findViewById(R.id.newCategorySubmit);
        TextView titleTextView = view.findViewById(R.id.textViewCat);

        if (TYPE_SUGGEST.equals(creationType)) {
            titleTextView.setText("Suggest Category");
            submitButton.setText("Suggest");
        } else {
            titleTextView.setText("Create Category");
            submitButton.setText("Create");
        }

        submitButton.setOnClickListener(v -> {
            String categoryName = nameEditText.getText().toString().trim();
            String categoryDescription = descriptionEditText.getText().toString().trim();

            if (categoryName.isEmpty() || categoryDescription.isEmpty()) {
                Toast.makeText(getContext(), "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            } else {
                if (TYPE_SUGGEST.equals(creationType)) {
                    viewModel.createCategory(categoryName, categoryDescription, "SUGGEST");
                } else {
                    viewModel.createCategory(categoryName, categoryDescription, "CREATE");
                }
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int screenWidth = displayMetrics.widthPixels;

            int dialogWidth = (int) (screenWidth * 0.8);
            getDialog().getWindow().setBackgroundDrawableResource(R.drawable.form_frame_white);

            getDialog().getWindow().setLayout(dialogWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

}