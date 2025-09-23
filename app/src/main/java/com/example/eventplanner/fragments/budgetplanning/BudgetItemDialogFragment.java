package com.example.eventplanner.fragments.budgetplanning;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.eventplanner.R;
import com.example.eventplanner.dto.budget.GetBudgetItemDTO;
import com.example.eventplanner.dto.solutioncategory.GetCategoryDTO;
import com.example.eventplanner.viewmodels.BudgetPlanningViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BudgetItemDialogFragment extends DialogFragment {
    private static final String ARG_ITEM = "item";
    private static final String ARG_POSITION = "position";

    private EditText itemNameEditText;
    private EditText itemCostEditText;
    private Spinner categorySpinner;
    private Button saveButton;
    private List<GetCategoryDTO> allCategories;
    private GetBudgetItemDTO currentItem;
    private int currentPosition;

    public interface BudgetItemDialogListener {
        void onBudgetItemAdded(GetBudgetItemDTO newItem, GetCategoryDTO selectedCategory);
        void onBudgetItemUpdated(GetBudgetItemDTO updatedItem, GetCategoryDTO selectedCategory, int position);
    }

    private BudgetItemDialogListener listener;
    private BudgetPlanningViewModel viewModel;

    public static BudgetItemDialogFragment newInstance() {
        return new BudgetItemDialogFragment();
    }

    public static BudgetItemDialogFragment newInstance(GetBudgetItemDTO item, int position) {
        BudgetItemDialogFragment fragment = new BudgetItemDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_ITEM, item);
        args.putInt(ARG_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentItem = (GetBudgetItemDTO) getArguments().getSerializable(ARG_ITEM);
            currentPosition = getArguments().getInt(ARG_POSITION, -1);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_budget_item_dialog, container, false);

        itemNameEditText = view.findViewById(R.id.et_item_name);
        itemCostEditText = view.findViewById(R.id.et_item_cost);
        categorySpinner = view.findViewById(R.id.spinner_item_category);
        saveButton = view.findViewById(R.id.btn_save_item);

        viewModel = new ViewModelProvider(this).get(BudgetPlanningViewModel.class);

        allCategories = new ArrayList<>();

        viewModel.fetchAllActiveCategories();

        viewModel.getActiveCategories().observe(getViewLifecycleOwner(), categories -> {
            if (categories != null && !categories.isEmpty()) {
                allCategories = categories;
                List<String> categoryNames = categories.stream()
                        .map(GetCategoryDTO::getName)
                        .collect(Collectors.toList());

                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                        android.R.layout.simple_spinner_item,
                        categoryNames);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                categorySpinner.setAdapter(adapter);

                // Ako je mod izmena, postavi odabranu kategoriju
                if (currentItem != null && currentItem.getCategory() != null) {
                    int categoryIndex = categoryNames.indexOf(currentItem.getCategory().getName());
                    if (categoryIndex != -1) {
                        categorySpinner.setSelection(categoryIndex);
                    }
                    categorySpinner.setEnabled(false);
                }
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        if (currentItem != null) {
            itemNameEditText.setText(currentItem.getName());
            itemCostEditText.setText(String.valueOf(currentItem.getCost()));
            saveButton.setText("Update");
        } else {
            saveButton.setText("Save");
        }
        saveButton.setOnClickListener(v -> {
            String itemName = itemNameEditText.getText().toString().trim();
            String costString = itemCostEditText.getText().toString().trim();

            if (categorySpinner.getSelectedItem() == null) {
                Toast.makeText(getContext(), "Category is required.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!itemName.isEmpty() && !costString.isEmpty()) {
                try {
                    Double itemCost = Double.parseDouble(costString);

                    if (currentItem != null) { // Logika za izmenu
                        GetCategoryDTO originalCategory = currentItem.getCategory();

                        currentItem.setName(itemName);
                        currentItem.setCost(itemCost);
                        currentItem.setCategory(originalCategory);

                        if (listener != null) {
                            listener.onBudgetItemUpdated(currentItem, originalCategory, currentPosition);
                        }
                    } else { // Logika za kreiranje
                        GetCategoryDTO selectedCategory = allCategories.get(categorySpinner.getSelectedItemPosition());
                        GetBudgetItemDTO newBudgetItem = new GetBudgetItemDTO();
                        newBudgetItem.setName(itemName);
                        newBudgetItem.setCost(itemCost);
                        newBudgetItem.setCategory(selectedCategory);

                        if (listener != null) {
                            listener.onBudgetItemAdded(newBudgetItem, selectedCategory);
                        }
                    }
                    dismiss();
                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "Budget must be valid number.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    public void setBudgetItemDialogListener(BudgetItemDialogListener listener) {
        this.listener = listener;
    }
    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {
            Window window = getDialog().getWindow();
            if (window != null) {
                window.setBackgroundDrawableResource(R.drawable.form_frame_white);

                DisplayMetrics displayMetrics = new DisplayMetrics();
                getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int screenWidth = displayMetrics.widthPixels;

                int dialogWidth = (int) (screenWidth * 0.80);

                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                layoutParams.copyFrom(window.getAttributes());

                layoutParams.width = dialogWidth;
                layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                window.setAttributes(layoutParams);
            }
        }
    }
}