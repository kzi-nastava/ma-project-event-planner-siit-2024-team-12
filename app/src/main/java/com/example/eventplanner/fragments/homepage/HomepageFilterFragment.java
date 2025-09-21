package com.example.eventplanner.fragments.homepage;

import android.content.Context; // Add this import
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.eventplanner.R;
import com.example.eventplanner.activities.homepage.HomepageService;
import com.example.eventplanner.utils.ClientUtils;
import com.example.eventplanner.viewmodels.EventFilterViewModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomepageFilterFragment extends Fragment {

    private ChipGroup chipGroup;
    private List<String> availableCategories = new ArrayList<>();
    private HomepageService service;

    public HomepageFilterFragment() {
        // Required empty public constructor
    }

    public static HomepageFilterFragment newInstance() {
        return new HomepageFilterFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_homepage_filter, container, false);
    }

    private EventFilterViewModel filterViewModel;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        chipGroup = view.findViewById(R.id.chipGroup);

        filterViewModel = new ViewModelProvider(requireActivity()).get(EventFilterViewModel.class);
        filterViewModel.getAppliedFilters().observe(getViewLifecycleOwner(), payload -> {

            updateChips(payload);
        });

        Button openFilterBtn = view.findViewById(R.id.filterButton);
        openFilterBtn.setOnClickListener(v -> {
            EventFilterFragment dialog = new EventFilterFragment();
            dialog.show(getParentFragmentManager(), "FilterDialog");
        });


    }


    private void updateChips(EventFilterViewModel.FilterPayload p) {
        chipGroup.removeAllViews();

        for (String c : p.cities) addFilterChip("City: " + c, () -> {
            filterViewModel.removeCity(c);
            filterViewModel.applyNow();
        });
        for (String t : p.eventTypes) addFilterChip("Type: " + t, () -> {
            filterViewModel.removeEventType(t);
            filterViewModel.applyNow();
        });
        if (p.rating != null) addFilterChip("Rating: " + p.rating, () -> {
            filterViewModel.setSelectedRating(null);
            filterViewModel.applyNow();
        });
        if (p.sortBy != null && !p.sortBy.isEmpty()) addFilterChip("Sort: " + p.sortBy, () -> {
            filterViewModel.setSelectedSortOptions(null);
            filterViewModel.applyNow();
        });
        if (p.sortDir != null && !p.sortDir.isEmpty()) addFilterChip("Dir: " + p.sortDir, () -> {
            filterViewModel.setSortDir(null);
            filterViewModel.applyNow();
        });
        if (p.startDate != null && !p.startDate.isEmpty()) addFilterChip("From: " + p.startDate, () -> {
            filterViewModel.setMinDate("");
            filterViewModel.applyNow();
        });
        if (p.endDate != null && !p.endDate.isEmpty()) addFilterChip("To: " + p.endDate, () -> {
            filterViewModel.setMaxDate("");
            filterViewModel.applyNow();
        });
    }

    private void addFilterChip(String text, Runnable onRemove) {
        Chip chip = new Chip(getContext());
        chip.setText(text);
        chip.setCloseIconVisible(true);
        chip.setOnCloseIconClickListener(v -> {
            chipGroup.removeView(chip);
            onRemove.run();
        });
        chipGroup.addView(chip);
    }

}