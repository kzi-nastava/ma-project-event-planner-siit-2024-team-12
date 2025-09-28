package com.example.eventplanner.fragments.homepage;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.adapters.SingleSelectAdapter;
import com.example.eventplanner.services.HomepageService;
import com.example.eventplanner.utils.ClientUtils;
import com.example.eventplanner.viewmodels.EventFilterViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventFilterFragment extends DialogFragment {

    private List<String> cityOptions = new ArrayList<>();
    private List<String> eventTypeOptions = new ArrayList<>();
    private List<String> ratingOptions = new ArrayList<>();
    private List<String> sortByOptions = new ArrayList<>();
    private List<String> sortDirectionOptions = new ArrayList<>();

    private Button filterBtn;
    private EventFilterViewModel filterViewModel;
    private View view;
    private TextView startDateText, endDateText;

    private final Map<String, Integer> filterIcons = new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_event_filter, container, false);

        filterViewModel = new ViewModelProvider(requireParentFragment()).get(EventFilterViewModel.class);

        startDateText = view.findViewById(R.id.startDate);
        endDateText = view.findViewById(R.id.endDate);

        setupDatePickers();
        loadAllEventFilters();

        filterBtn = view.findViewById(R.id.filterBtn);
        filterBtn.setOnClickListener(v -> applyFilters());
        View cityFilterLayout = view.findViewById(R.id.cityFilter);

        filterViewModel.getIgnoreCityFilter().observe(getViewLifecycleOwner(), ignoreCity -> {
            Boolean privileged = filterViewModel.getIsPrivileged().getValue();
            boolean shouldDisableCityFilter = Boolean.TRUE.equals(privileged) && Boolean.FALSE.equals(ignoreCity);

            cityFilterLayout.setAlpha(shouldDisableCityFilter ? 0.5f : 1.0f);
            cityFilterLayout.setEnabled(!shouldDisableCityFilter);
            cityFilterLayout.setClickable(!shouldDisableCityFilter);

            RecyclerView recyclerView = cityFilterLayout.findViewById(R.id.options);
            if (recyclerView != null) {
                if (shouldDisableCityFilter) {
                    recyclerView.setVisibility(View.GONE);
                    filterViewModel.setSelectedCities(null);
                } else {
                    cityFilterLayout.setOnClickListener(v -> {
                        if (recyclerView.getVisibility() == View.GONE) {
                            cityFilterLayout.findViewById(R.id.expandArrow).setRotation(180f);
                            recyclerView.setVisibility(View.VISIBLE);
                        } else {
                            recyclerView.setVisibility(View.GONE);
                            cityFilterLayout.findViewById(R.id.expandArrow).setRotation(0f);
                        }
                    });
                }
            }
        });


        return view;
    }

    private void setupDatePickers() {
        Calendar calendar = Calendar.getInstance();

        startDateText.setOnClickListener(v -> {
            DatePickerDialog dpd = new DatePickerDialog(getContext(), (view, year, month, day) -> {
                startDateText.setText(String.format("%04d-%02d-%02d", year, month + 1, day));
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            dpd.show();
        });

        endDateText.setOnClickListener(v -> {
            DatePickerDialog dpd = new DatePickerDialog(getContext(), (view, year, month, day) -> {
                endDateText.setText(String.format("%04d-%02d-%02d", year, month + 1, day));
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            dpd.show();
        });
    }

    private void applyFilters() {
        String selectedCity = ((SingleSelectAdapter) ((RecyclerView) view.findViewById(R.id.cityFilter)
                .findViewById(R.id.options)).getAdapter()).getSelectedItem();

        String selectedEventType = ((SingleSelectAdapter) ((RecyclerView) view.findViewById(R.id.eventTypeFilter)
                .findViewById(R.id.options)).getAdapter()).getSelectedItem();

        String selectedRating = ((SingleSelectAdapter) ((RecyclerView) view.findViewById(R.id.ratingFilter)
                .findViewById(R.id.options)).getAdapter()).getSelectedItem();

        String selectedSortBy = ((SingleSelectAdapter) ((RecyclerView) view.findViewById(R.id.sortByFilter)
                .findViewById(R.id.options)).getAdapter()).getSelectedItem();

        String selectedSortDir = ((SingleSelectAdapter) ((RecyclerView) view.findViewById(R.id.sortDirectionFilter)
                .findViewById(R.id.options)).getAdapter()).getSelectedItem();

        EditText maxGuestsInput = view.findViewById(R.id.maxGuests);
        Integer maxGuestsValue = null;
        try {
            String maxStr = maxGuestsInput.getText().toString().trim();
            if (!maxStr.isEmpty()) {
                maxGuestsValue = Integer.parseInt(maxStr);
            }
        } catch (NumberFormatException e) {
            maxGuestsValue = null;
        }
        filterViewModel.setMaxGuests(maxGuestsValue);

        // Set into ViewModel
        filterViewModel.setSelectedCities(selectedCity != null ? List.of(selectedCity) : new ArrayList<>());
        filterViewModel.setSelectedEventTypes(selectedEventType != null ? List.of(selectedEventType) : new ArrayList<>());
        filterViewModel.setSelectedRating(selectedRating != null ? Integer.parseInt(selectedRating) : null);
        filterViewModel.setSelectedSortOptions(selectedSortBy);
        filterViewModel.setSortDir(selectedSortDir);

        filterViewModel.setMinDate(startDateText.getText().toString());
        filterViewModel.setMaxDate(endDateText.getText().toString());

        filterViewModel.applyNow();
        dismiss();
    }

    private void setUpExistingFilters() {
        setupSingleSelectFilter(view, R.id.cityFilter, "Location", cityOptions,
                filterViewModel.getSelectedCities().getValue() != null && !filterViewModel.getSelectedCities().getValue().isEmpty()
                        ? filterViewModel.getSelectedCities().getValue().get(0) : null);

        setupSingleSelectFilter(view, R.id.eventTypeFilter, "Event Type", eventTypeOptions,
                filterViewModel.getSelectedEventTypes().getValue() != null && !filterViewModel.getSelectedEventTypes().getValue().isEmpty()
                        ? filterViewModel.getSelectedEventTypes().getValue().get(0) : null);

        setupSingleSelectFilter(view, R.id.ratingFilter, "Rating", ratingOptions,
                filterViewModel.getSelectedRating().getValue() != null
                        ? String.valueOf(filterViewModel.getSelectedRating().getValue()) : null);

        setupSingleSelectFilter(view, R.id.sortByFilter, "Sort by", sortByOptions, filterViewModel.getSelectedSortOptions().getValue());
        setupSingleSelectFilter(view, R.id.sortDirectionFilter, "Sort direction", sortDirectionOptions, filterViewModel.getSortDir().getValue());

        startDateText.setText(filterViewModel.getMinDate().getValue() != null ? filterViewModel.getMinDate().getValue() : "");
        endDateText.setText(filterViewModel.getMaxDate().getValue() != null ? filterViewModel.getMaxDate().getValue() : "");
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

    private void loadAllEventFilters() {
        HomepageService service = ClientUtils.retrofit.create(HomepageService.class);
        service.getAvailableEventFilters().enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(@NonNull Call<Map<String, Object>> call, @NonNull Response<Map<String, Object>> response) {
                if (!isAdded()) return;
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> filters = response.body();
                    if (filters.get("eventTypes") instanceof List)
                        eventTypeOptions.addAll((List<String>) filters.get("eventTypes"));
                    if (filters.get("cities") instanceof List)
                        cityOptions.addAll((List<String>) filters.get("cities"));
                    if (filters.get("ratings") instanceof List) {
                        List<Double> ratings = (List<Double>) filters.get("ratings");
                        for (Double r : ratings) ratingOptions.add(String.valueOf(r.intValue()));
                    }
                    if (filters.get("sortOptions") instanceof List) {
                        List<String> sortOptions = (List<String>) filters.get("sortOptions");
                        sortByOptions.clear();
                        for (String s : sortOptions) {
                            if (s.equalsIgnoreCase("price")) {
                                sortByOptions.add(toAllUpperCase("Max Guests"));
                            } else {
                                sortByOptions.add(toAllUpperCase(s));
                            }
                        }
                        sortDirectionOptions.clear();
                        sortDirectionOptions.add("ASC");
                        sortDirectionOptions.add("DESC");
                    }


                    setUpExistingFilters();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Map<String, Object>> call, @NonNull Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(getContext(), "Greška u mreži: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void setUpFilterIcons() {
        filterIcons.put("Location", R.drawable.ic_location);
        filterIcons.put("Event Type", R.drawable.celebration);
        filterIcons.put("Rating", R.drawable.ic_rating);
        filterIcons.put("Sort by", R.drawable.ic_sort);
        filterIcons.put("Sort direction", R.drawable.ic_sort_dir);
    }

    private String toAllUpperCase(String input) {
        if (input == null) return null;
        return input.toUpperCase(Locale.getDefault());
    }
}
