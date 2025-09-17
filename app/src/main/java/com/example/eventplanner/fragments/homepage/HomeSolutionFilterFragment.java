package com.example.eventplanner.fragments.homepage;

import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eventplanner.R;
import com.example.eventplanner.adapters.SingleSelectAdapter;
import com.example.eventplanner.utils.ClientUtils;
import com.example.eventplanner.viewmodels.HomeSolutionFilterViewModel;
import com.example.eventplanner.activities.homepage.HomepageService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeSolutionFilterFragment extends DialogFragment {

    private List<String> categoryOptions = new ArrayList<>();
    private List<String> eventTypeOptions = new ArrayList<>();
    private List<String> ratingOptions = new ArrayList<>();
    private List<String> sortByOptions = new ArrayList<>();
    private Button filterBtn;
    private HomeSolutionFilterViewModel filterViewModel;
    private View view;
    private EditText minPrice, maxPrice, minDiscount, maxDiscount;
    private Map<String, Integer> filterIcons = new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home_solution_filter, container, false);
        filterViewModel = new ViewModelProvider(requireActivity()).get(HomeSolutionFilterViewModel.class);

        minPrice = view.findViewById(R.id.minPrice);
        maxPrice = view.findViewById(R.id.maxPrice);
        minDiscount = view.findViewById(R.id.minDiscount);
        maxDiscount = view.findViewById(R.id.maxDiscount);

        loadAvailableFiltersFromBackend();

        setUpExistingFilters();

        filterBtn = view.findViewById(R.id.filterBtn);
        filterBtn.setOnClickListener(v -> applyFilters());

        return view;
    }

    private void loadAvailableFiltersFromBackend() {
        HomepageService service = ClientUtils.retrofit.create(HomepageService.class);
        Call<Map<String, Object>> call = service.getAvailableSolutionFilters();
        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> filters = response.body();
                    if (filters.get("categories") != null) {
                        categoryOptions = (List<String>) filters.get("categories");
                    }
                    if (filters.get("eventTypes") != null) {
                        eventTypeOptions = (List<String>) filters.get("eventTypes");
                    }
                    if (filters.get("ratings") != null) {
                        ratingOptions = new ArrayList<>();
                        List<Double> ratingsDouble = (List<Double>) filters.get("ratings");
                        for (Double rating : ratingsDouble) {
                            ratingOptions.add(String.valueOf(rating.intValue()));
                        }
                    }
                    if (filters.get("sortOptions") != null) {
                        List<String> rawSortOptions = (List<String>) filters.get("sortOptions");
                        sortByOptions = new ArrayList<>();
                        for (String option : rawSortOptions) {
                            sortByOptions.add(option.substring(0, 1).toUpperCase() + option.substring(1) + " " + "ASC");
                            sortByOptions.add(option.substring(0, 1).toUpperCase() + option.substring(1) + " " + "DESC");
                        }
                    }
                    setUpExistingFilters();
                } else {
                    Toast.makeText(getActivity(), "Error loading filters!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(getActivity(), "Failed to load filters!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void applyFilters() {
        String selectedCategory = ((SingleSelectAdapter)((RecyclerView) view.findViewById(R.id.categoryFilter).findViewById(R.id.options)).getAdapter()).getSelectedItem();
        String selectedEventType = ((SingleSelectAdapter)((RecyclerView) view.findViewById(R.id.eventTypeFilter).findViewById(R.id.options)).getAdapter()).getSelectedItem();
        String selectedRating = ((SingleSelectAdapter)((RecyclerView) view.findViewById(R.id.ratingFilter).findViewById(R.id.options)).getAdapter()).getSelectedItem();
        String selectedSortBy = ((SingleSelectAdapter)((RecyclerView) view.findViewById(R.id.sortByFilter).findViewById(R.id.options)).getAdapter()).getSelectedItem();

        filterViewModel.setSelectedCategory(selectedCategory);
        filterViewModel.setSelectedEventType(selectedEventType);

        if (selectedRating != null) {
            filterViewModel.setRating(Double.parseDouble(selectedRating));
        } else {
            filterViewModel.setRating(null);
        }

        if (selectedSortBy != null) {
            String[] sortOptions = selectedSortBy.split(" ");
            filterViewModel.setSortBy(sortOptions[0].toLowerCase());
            filterViewModel.setSortDir(sortOptions[1].toUpperCase());
        } else {
            filterViewModel.setSortBy(null);
            filterViewModel.setSortDir(null);
        }

        setUpPriceAndDiscountRange();
        filterViewModel.applyNow();
        dismiss();
    }

    private void setUpExistingFilters() {
        setupSingleSelectFilter(view, R.id.categoryFilter, getString(R.string.category), categoryOptions, filterViewModel.getSelectedCategory().getValue());
        setupSingleSelectFilter(view, R.id.eventTypeFilter, getString(R.string.event_type_filter), eventTypeOptions, filterViewModel.getSelectedEventType().getValue());
        setupSingleSelectFilter(view, R.id.ratingFilter, getString(R.string.rating), ratingOptions, filterViewModel.getRating().getValue() != null ? String.valueOf(filterViewModel.getRating().getValue().intValue()) : null);
        setupSingleSelectFilter(view, R.id.sortByFilter, getString(R.string.sort_by), sortByOptions, filterViewModel.getSortBy().getValue() != null ? filterViewModel.getSortBy().getValue().substring(0, 1).toUpperCase() + filterViewModel.getSortBy().getValue().substring(1) + " " + filterViewModel.getSortDir().getValue() : null);

        Double minPriceVal = filterViewModel.getMinPrice().getValue();
        Double maxPriceVal = filterViewModel.getMaxPrice().getValue();
        Double minDiscountVal = filterViewModel.getMinDiscount().getValue();
        Double maxDiscountVal = filterViewModel.getMaxDiscount().getValue();

        minPrice.setText(minPriceVal != null ? String.valueOf(minPriceVal) : "");
        maxPrice.setText(maxPriceVal != null ? String.valueOf(maxPriceVal) : "");
        minDiscount.setText(minDiscountVal != null ? String.valueOf(minDiscountVal.intValue()) : "");
        maxDiscount.setText(maxDiscountVal != null ? String.valueOf(maxDiscountVal.intValue()) : "");
    }

    private void setupSingleSelectFilter(View parentView, int filterId, String filterName, List<String> options, String selectedItem) {
        View filterView = parentView.findViewById(filterId);
        TextView parameterName = filterView.findViewById(R.id.parameterName);

        parameterName.setText(filterName);

        setUpFilterIcons();

        Integer iconResId = filterIcons.get(filterName);
        if (iconResId != null) {
            parameterName.setCompoundDrawablesWithIntrinsicBounds(iconResId, 0, 0, 0);
            parameterName.setCompoundDrawablePadding(20);
        }

        RecyclerView recyclerView = filterView.findViewById(R.id.options);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        SingleSelectAdapter adapter = new SingleSelectAdapter(options);
        recyclerView.setAdapter(adapter);

        if (selectedItem != null) {
            adapter.setSelectedItem(selectedItem);
        }

        filterView.setOnClickListener(v -> {
            if (recyclerView.getVisibility() == View.GONE) {
                filterView.findViewById(R.id.expandArrow).setRotation(180f);
                recyclerView.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setVisibility(View.GONE);
                filterView.findViewById(R.id.expandArrow).setRotation(0f);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {
            Objects.requireNonNull(getDialog().getWindow()).setLayout(1000, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    private void setUpPriceAndDiscountRange() {
        try {
            filterViewModel.setMinPrice(minPrice.getText().toString().isEmpty() ? null : Double.parseDouble(minPrice.getText().toString()));
        } catch (NumberFormatException e) {
            filterViewModel.setMinPrice(null);
        }
        try {
            filterViewModel.setMaxPrice(maxPrice.getText().toString().isEmpty() ? null : Double.parseDouble(maxPrice.getText().toString()));
        } catch (NumberFormatException e) {
            filterViewModel.setMaxPrice(null);
        }
        try {
            filterViewModel.setMinDiscount(minDiscount.getText().toString().isEmpty() ? null : Double.parseDouble(minDiscount.getText().toString()));
        } catch (NumberFormatException e) {
            filterViewModel.setMinDiscount(null);
        }
        try {
            filterViewModel.setMaxDiscount(maxDiscount.getText().toString().isEmpty() ? null : Double.parseDouble(maxDiscount.getText().toString()));
        } catch (NumberFormatException e) {
            filterViewModel.setMaxDiscount(null);
        }
    }

    private void setUpFilterIcons() {
        filterIcons.put(requireContext().getString(R.string.category), R.drawable.category_filled);
        filterIcons.put(getString(R.string.event_type_filter), R.drawable.celebration);
        filterIcons.put(requireContext().getString(R.string.rating), R.drawable.ic_rating);
        filterIcons.put(requireContext().getString(R.string.sort_by), R.drawable.ic_sort);
    }
}