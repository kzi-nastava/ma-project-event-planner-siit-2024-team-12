package com.example.eventplanner.fragments.homepage;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.eventplanner.activities.homepage.CardItem;
import com.example.eventplanner.adapters.homepage.ListItemAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;

import com.example.eventplanner.viewmodels.EventFilterViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseListFragment<T extends CardItem, V extends ViewModel> extends Fragment {

    protected RecyclerView recyclerView;
    protected AppCompatImageButton prevPageButton, nextPageButton;
    protected LinearLayout paginationIndicators;
    protected ChipGroup chipGroup;
    protected SearchView searchView;
    protected MaterialButton onlyFromMyCityBtn;
    protected TextView listTitle;
    protected RadioGroup solutionTypeRadioGroup;
    protected LinearLayout filterButtonsLayout;
    protected TextView emptyMessage;

    protected V filterViewModel;
    protected List<T> allItems = new ArrayList<>();
    protected int currentPage = 0;
    protected final int pageSize = 2;

    protected abstract int getLayoutResId();
    protected abstract void setupViewModel();
    protected abstract RecyclerView.Adapter<?> createAdapter();
    protected abstract void loadItemsFromBackend(Object payloadObj);
    protected abstract void showFilterDialog();
    protected abstract void addSearchChip(String query);

    protected abstract void resetFilters();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutResId(), container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        prevPageButton = view.findViewById(R.id.prevPageButton);
        nextPageButton = view.findViewById(R.id.nextPageButton);
        paginationIndicators = view.findViewById(R.id.paginationIndicators);
        chipGroup = view.findViewById(R.id.chipGroup);
        searchView = view.findViewById(R.id.searchView);
        onlyFromMyCityBtn = view.findViewById(R.id.onlyFromMyCityButton);

        listTitle = view.findViewById(R.id.listTitle);
        solutionTypeRadioGroup = view.findViewById(R.id.solutionTypeRadioGroup);
        filterButtonsLayout = view.findViewById(R.id.filterButtonsLayout);
        emptyMessage = view.findViewById(R.id.emptyStateText);

        setupViewModel();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(createAdapter());

        setupPaginationButtons();
        setupSearchView();
        setupFilterButtons(view);

        loadInitialData();

        return view;
    }

    protected void loadInitialData() {
        loadItemsFromBackend(null);
    }

    protected void setupPaginationButtons() {
        if (prevPageButton != null) {
            prevPageButton.setOnClickListener(v -> {
                if (currentPage > 0) {
                    currentPage--;
                    updateRecyclerView();
                }
            });
        }
        if (nextPageButton != null) {
            nextPageButton.setOnClickListener(v -> {
                if ((currentPage + 1) * pageSize < allItems.size()) {
                    currentPage++;
                    updateRecyclerView();
                }
            });
        }
    }

    protected void setupSearchView() {
        if (searchView != null) {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    String submittedQuery = query.trim();
                    if (!submittedQuery.isEmpty()) {
                        addSearchChip(submittedQuery);
                        searchView.setQuery("", false);
                        searchView.clearFocus();
                        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm != null) {
                            imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
                        }
                    }
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });
        }
    }

    protected void setupFilterButtons(View view) {
        if (filterButtonsLayout.getVisibility() == View.VISIBLE) {
            Button resetFiltersButton = view.findViewById(R.id.resetFiltersButton);
            if (resetFiltersButton != null) {
                resetFiltersButton.setOnClickListener(v -> resetFilters());
            }

            Button filterButton = view.findViewById(R.id.filterButton);
            if (filterButton != null) {
                filterButton.setOnClickListener(v -> showFilterDialog());
            }
        }
    }

    protected void updateRecyclerView() {
        int totalPages = (int) Math.ceil((double) allItems.size() / pageSize);
        int start = currentPage * pageSize;
        int end = Math.min(start + pageSize, allItems.size());

        if (recyclerView.getAdapter() != null) {
            List<T> sublist = allItems.subList(start, end);
            ((ListItemAdapter) recyclerView.getAdapter()).setItems(sublist);
        }

        updatePaginationIndicators(totalPages);
    }

    protected void updatePaginationIndicators(int totalPages) {
        if (getView() == null || paginationIndicators == null) return;
        paginationIndicators.removeAllViews();
        TextView pageInfo = new TextView(getContext());
        pageInfo.setText((currentPage + 1) + " / " + totalPages);
        pageInfo.setTextSize(14);
        pageInfo.setTextColor(Color.DKGRAY);
        pageInfo.setGravity(Gravity.CENTER);
        paginationIndicators.addView(pageInfo);
    }

    protected void addFilterChip(String text, Runnable onRemove) {
        if (chipGroup == null) return;
        Chip chip = new Chip(getContext());
        chip.setText(text);
        chip.setCloseIconVisible(true);
        chip.setOnCloseIconClickListener(v -> {
            chipGroup.removeView(chip);
            onRemove.run();
        });
        chipGroup.addView(chip);
    }
    protected void updateButtonAppearance(MaterialButton button, boolean isActive) {
        if (button == null) return;
        int strokeColor = isActive
                ? ContextCompat.getColor(requireContext(), R.color.white)
                : ContextCompat.getColor(requireContext(), R.color.light_gray);
        int textColor = isActive
                ? ContextCompat.getColor(requireContext(), R.color.dark_gray)
                : ContextCompat.getColor(requireContext(), R.color.black);
        button.setStrokeColor(ColorStateList.valueOf(strokeColor));
        button.setTextColor(textColor);
        int bgColor = isActive
                ? ContextCompat.getColor(requireContext(), R.color.activeButtonBackground)
                : ContextCompat.getColor(requireContext(), R.color.inactiveButtonBackground);
        button.setBackgroundColor(bgColor);
    }
}