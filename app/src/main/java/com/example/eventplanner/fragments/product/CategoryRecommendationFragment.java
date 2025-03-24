package com.example.eventplanner.fragments.product;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.eventplanner.R;
import com.example.eventplanner.model.CategoryRecommendation;
import com.example.eventplanner.utils.ValidationUtils;
import com.example.eventplanner.viewmodels.ProductCreationViewModel;


public class CategoryRecommendationFragment extends DialogFragment {

    private View view;
    private ProductCreationViewModel viewModel;
    private TextView saveTxt, cancelTxt;
    private EditText nameField, descriptionField;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_category_recommendation, container, false);

        viewModel = new ViewModelProvider(requireActivity()).get(ProductCreationViewModel.class);

        cancelTxt = view.findViewById(R.id.cancelTxt);
        saveTxt = view.findViewById(R.id.saveTxt);

        cancelTxt.setOnClickListener(v -> {
            dismiss();
        });


        saveTxt.setOnClickListener(v -> {
            saveCategoryRecommendation();
        });

        return view;
    }


    private void saveCategoryRecommendation() {
        nameField = view.findViewById(R.id.name);
        descriptionField = view.findViewById(R.id.description);

        if (!ValidationUtils.isFieldValid(nameField, "Name is required!")) return;
        if (!ValidationUtils.isFieldValid(descriptionField, "Description is required!")) return;

        CategoryRecommendation recommendation = new CategoryRecommendation(nameField.getText().toString(),
                descriptionField.getText().toString());

        viewModel.updateCategoryRecommendation(recommendation);
        viewModel.usedRecommendation = true;

        dismiss();

    }
}