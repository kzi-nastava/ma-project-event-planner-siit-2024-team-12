package com.example.eventplanner.fragments.homepage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;

import androidx.fragment.app.Fragment;

import com.example.eventplanner.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

public class HomepageFilterFragment extends Fragment {

    public HomepageFilterFragment() {
        // Required empty public constructor
    }

    public static HomepageFilterFragment newInstance() {
        return new HomepageFilterFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_homepage_filter, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Spinner za kategorije (ako ga koristiš, ovde se može sakriti ili prikazivati)
        Spinner categorySpinner = view.findViewById(R.id.categorySpinner);
        categorySpinner.setVisibility(View.GONE); // Početno sakrivanje

        // ChipGroup za izabrane filtere
        ChipGroup chipGroup = view.findViewById(R.id.chipGroup);

        // Filter dugme - toggle spinner za kategorije
        TextView filterButton = view.findViewById(R.id.filterButton);
        filterButton.setOnClickListener(v -> {
            // Pozivanje dijaloga za višestruki izbor
            showCategoryDialog(chipGroup);
        });

        // Dugme za resetovanje filtera
        Button resetFiltersButton = view.findViewById(R.id.resetFiltersButton);
        resetFiltersButton.setOnClickListener(v -> {
            chipGroup.clearCheck(); // Čisti selektovane filtere
            Toast.makeText(getContext(), "Filters reset", Toast.LENGTH_SHORT).show();
        });
    }

    private void showCategoryDialog(ChipGroup chipGroup) {
        final String[] categories = {"Category 1", "Category 2", "Category 3", "Category 4"};
        boolean[] checkedItems = new boolean[categories.length]; // Initial state: nothing selected

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.CustomDialogStyle); // Using custom style
        builder.setTitle("Select Categories")
                .setMultiChoiceItems(categories, checkedItems, (dialog, which, isChecked) -> {
                    // Track the changes
                    checkedItems[which] = isChecked;
                })
                .setPositiveButton("OK", (dialog, which) -> {
                    // Update the chips with selected categories
                    updateFilters(checkedItems, categories, chipGroup);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .setCancelable(true);

        // Create the dialog and show it
        AlertDialog dialog = builder.create();
        dialog.show();

        // Adjust dialog width and height
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.6); // 60% of screen width
        //layoutParams.height = (int) (getResources().getDisplayMetrics().heightPixels * 0.4); // 40% of screen height
        dialog.getWindow().setAttributes(layoutParams);

        // Style the dialog buttons
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.black));
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.black));

        ListView listView = dialog.getListView();
        listView.setDivider(null); // Remove dividers between items
    }


    // Funkcija za dinamičko dodavanje filtera prema izabranim kategorijama
    private void updateFilters(boolean[] checkedItems, String[] categories, ChipGroup chipGroup) {
        chipGroup.removeAllViews(); // Briše postojeće filtere

        // Dodavanje filtera za svaku izabranu kategoriju
        for (int i = 0; i < categories.length; i++) {
            if (checkedItems[i]) {
                addFilterChip(categories[i], chipGroup);
            }
        }
    }

    // Funkcija za dodavanje novog filter chip-a.java
    private void addFilterChip(String filterText, ChipGroup chipGroup) {
        Chip chip = new Chip(getContext());
        chip.setText(filterText);
        chip.setCloseIconVisible(true);
        chip.setOnCloseIconClickListener(v -> chipGroup.removeView(chip)); // Omogućava uklanjanje chip-a.java
        chipGroup.addView(chip);
    }
}
