package com.example.eventplanner.fragments.budgetplanning;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private Button cancelButton;
    private List<GetCategoryDTO> allCategories;
    private GetBudgetItemDTO currentItem; // Trenutni item za uređivanje
    private int currentPosition;

    // Za komunikaciju sa glavnim fragmentom
    public interface BudgetItemDialogListener {
        void onBudgetItemAdded(GetBudgetItemDTO newItem, GetCategoryDTO selectedCategory);
        void onBudgetItemUpdated(GetBudgetItemDTO updatedItem, GetCategoryDTO selectedCategory, int position);
    }

    private BudgetItemDialogListener listener;
    private BudgetPlanningViewModel viewModel;

    public static BudgetItemDialogFragment newInstance() {
        return new BudgetItemDialogFragment();
    }
    // Metoda za kreiranje dijaloga za izmenu
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

        // Inicijalizacija View-ova
        itemNameEditText = view.findViewById(R.id.et_item_name);
        itemCostEditText = view.findViewById(R.id.et_item_cost);
        categorySpinner = view.findViewById(R.id.spinner_item_category);
        saveButton = view.findViewById(R.id.btn_save_item);
        cancelButton = view.findViewById(R.id.btn_cancel);

        viewModel = new ViewModelProvider(this).get(BudgetPlanningViewModel.class);

        allCategories = new ArrayList<>();

        // 1. Pozivamo metodu za dohvat svih aktivnih kategorija
        viewModel.fetchAllActiveCategories();

        // 2. Posmatramo promene u listi kategorija
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
                    categorySpinner.setEnabled(false); // Onemogući spinner
                }
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        // Postavi postojeće podatke ako je mod izmena
        if (currentItem != null) {
            itemNameEditText.setText(currentItem.getName());
            itemCostEditText.setText(String.valueOf(currentItem.getCost()));
            saveButton.setText("Update");
        } else {
            saveButton.setText("Save");
        }
        // Logika za dugme Sačuvaj/Ažuriraj
        saveButton.setOnClickListener(v -> {
            String itemName = itemNameEditText.getText().toString().trim();
            String costString = itemCostEditText.getText().toString().trim();

            if (categorySpinner.getSelectedItem() == null) {
                Toast.makeText(getContext(), "Kategorija je obavezna.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!itemName.isEmpty() && !costString.isEmpty()) {
                try {
                    Double itemCost = Double.parseDouble(costString);

                    if (currentItem != null) { // Logika za izmenu
                        GetCategoryDTO originalCategory = currentItem.getCategory();

                        currentItem.setName(itemName);
                        currentItem.setCost(itemCost);
                        // Ostavljamo originalnu kategoriju - ne postavljamo novu
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

        cancelButton.setOnClickListener(v -> dismiss());

        return view;
    }

    // Metoda za postavljanje listenera iz glavnog fragmenta
    public void setBudgetItemDialogListener(BudgetItemDialogListener listener) {
        this.listener = listener;
    }
}