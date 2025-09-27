package com.example.eventplanner.fragments.categories;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.eventplanner.R;
import com.example.eventplanner.dto.solutioncategory.GetCategoryDTO;
import com.example.eventplanner.dto.solutioncategory.UpdateCategoryDTO;
import com.example.eventplanner.enumeration.Status;
import com.example.eventplanner.viewmodels.CategoryViewModel;

import java.io.Serializable;
import java.util.List;

public class CategoryDetailsFragment extends DialogFragment {

    private static final String ARG_CATEGORY = "category";
    private static final String ARG_IS_ACTIVE = "is_active";
    private CategoryViewModel viewModel;
    private EditText nameEditText;
    private EditText descriptionEditText;

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
        viewModel = new ViewModelProvider(requireActivity()).get(CategoryViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category_details, container, false);

        nameEditText = view.findViewById(R.id.textViewCategoryDetailsName);
        descriptionEditText = view.findViewById(R.id.textViewCategoryDetailsDescription);
        Button deleteButton = view.findViewById(R.id.buttonDeleteCategory);
        Button actionButton = view.findViewById(R.id.buttonActionCategory);

        if (category != null) {
            nameEditText.setText(category.getName());
            descriptionEditText.setText(category.getDescription());
        }

        if (isActive) {
            actionButton.setText("Save");
            actionButton.setOnClickListener(v -> {
                if(nameEditText.getText().toString().isEmpty() || descriptionEditText.getText().toString().isEmpty()){
                    Toast.makeText(getContext(), "Please fill in all fields.", Toast.LENGTH_SHORT).show();
                    return;
                }
                String updatedName = nameEditText.getText().toString();
                String updatedDescription = descriptionEditText.getText().toString();

                UpdateCategoryDTO updateDto = new UpdateCategoryDTO(updatedName, updatedDescription, Status.valueOf(category.getStatus()));

                if (category != null && category.getId() != null) {
                    viewModel.updateCategory(Long.valueOf(category.getId()), updateDto);
                    dismiss();
                }
            });
            deleteButton.setOnClickListener(v -> {
                if (category != null && category.getId() != null) {
                    viewModel.deleteCategory(Long.valueOf(category.getId()));
                    dismiss();
                }
            });

        } else {
            actionButton.setText("Approve");
            actionButton.setOnClickListener(v -> {
                if(nameEditText.getText().toString().isEmpty() || descriptionEditText.getText().toString().isEmpty()){
                    Toast.makeText(getContext(), "Please fill in all fields.", Toast.LENGTH_SHORT).show();
                    return;
                }
                String updatedName = nameEditText.getText().toString();
                String updatedDescription = descriptionEditText.getText().toString();
                UpdateCategoryDTO updateDto = new UpdateCategoryDTO(updatedName, updatedDescription, Status.ACCEPTED);

                if (category != null && category.getId() != null) {
                    viewModel.approveCategory(Long.valueOf(category.getId()), updateDto);
                    dismiss();
                }
            });
            deleteButton.setOnClickListener(v -> {
                List<GetCategoryDTO> activeCategoryList = viewModel.getActiveCategories().getValue();

                if (activeCategoryList == null || activeCategoryList.isEmpty()) {
                    Toast.makeText(getContext(), "No active categories to reassign.", Toast.LENGTH_SHORT).show();
                    return;
                }

                String[] categoryNames = new String[activeCategoryList.size()];
                for (int i = 0; i < activeCategoryList.size(); i++) {
                    categoryNames[i] = activeCategoryList.get(i).getName();
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Reassign to Active Category");
                builder.setItems(categoryNames, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String selectedCategoryName = categoryNames[which];

                        viewModel.disapproveCategory(Long.valueOf(category.getId()), selectedCategoryName);

                        dialog.dismiss();
                        dismiss();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.create().show();
            });

        }

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