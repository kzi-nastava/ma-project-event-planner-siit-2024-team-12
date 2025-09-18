package com.example.eventplanner.fragments.homepage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.eventplanner.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import java.util.ArrayList;
import java.util.List;

public class CategoryFilterFragment extends BottomSheetDialogFragment {

    private ChipGroup categoryChipGroup;
    private Button applyButton;
    private List<String> availableCategories; // Ovde ćemo smestiti podatke sa API-ja
    private CategoryFilterListener listener;

    // Interfejs za slanje odabranih filtera nazad
    public interface CategoryFilterListener {
        void onCategoriesApplied(List<String> selectedCategories);
    }

    public static CategoryFilterFragment newInstance(List<String> categories) {
        CategoryFilterFragment fragment = new CategoryFilterFragment();
        Bundle args = new Bundle();
        args.putStringArrayList("categories", new ArrayList<>(categories));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            availableCategories = getArguments().getStringArrayList("categories");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_category_filter, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        categoryChipGroup = view.findViewById(R.id.category_chip_group);
        applyButton = view.findViewById(R.id.apply_button);

        // Dinamičko dodavanje čipova iz API podataka
        for (String category : availableCategories) {
            /*Chip chip = (Chip) getLayoutInflater().inflate(R.layout.chip_filter, categoryChipGroup, false);
            chip.setText(category);
            chip.setCheckable(true);
            categoryChipGroup.addView(chip); */
        }

        applyButton.setOnClickListener(v -> {
            List<String> selectedCategories = new ArrayList<>();
            for (int i = 0; i < categoryChipGroup.getChildCount(); i++) {
                Chip chip = (Chip) categoryChipGroup.getChildAt(i);
                if (chip.isChecked()) {
                    selectedCategories.add(chip.getText().toString());
                }
            }
            if (listener != null) {
                listener.onCategoriesApplied(selectedCategories);
            }
            dismiss(); // Zatvori Bottom Sheet
        });
    }

    // Dodaj metodu za postavljanje Listenera
    public void setCategoryFilterListener(CategoryFilterListener listener) {
        this.listener = listener;
    }
}
