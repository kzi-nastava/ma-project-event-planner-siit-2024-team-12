package com.example.eventplanner.fragments.homepage;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.adapters.MultiSelectAdapter;
import com.example.eventplanner.activities.homepage.HomepageService;
import com.example.eventplanner.utils.ClientUtils;
import com.example.eventplanner.viewmodels.EventFilterViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventFilterFragment extends DialogFragment {

    private List<String> cityOptions = new ArrayList<>();
    private List<String> eventTypeOptions = new ArrayList<>();
    private List<String> ratingOptions = new ArrayList<>();
    private List<String> sortOptions = new ArrayList<>();
    private Button filterBtn;
    private EventFilterViewModel filterViewModel;
    private View view;
    private TextView startDateText, endDateText;
    private Spinner ratingSpinner, sortSpinner, sortDirSpinner;
    private final Map<String, Integer> filterIcons = new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_event_filter, container, false);

        filterViewModel = new ViewModelProvider(requireParentFragment()).get(EventFilterViewModel.class);

        startDateText = view.findViewById(R.id.startDate);
        endDateText = view.findViewById(R.id.endDate);
        ratingSpinner = view.findViewById(R.id.ratingSpinner);
        sortSpinner = view.findViewById(R.id.sortSpinner);
        sortDirSpinner = view.findViewById(R.id.sortDirSpinner);

        setupDatePickers();
        loadAllEventFilters();

        filterBtn = view.findViewById(R.id.filterBtn);
        filterBtn.setOnClickListener(v -> applyFilters());

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
        // Multi-select (city, event type)
        MultiSelectAdapter cityAd = (MultiSelectAdapter) ((RecyclerView) view.findViewById(R.id.cityFilter)
                .findViewById(R.id.options)).getAdapter();
        MultiSelectAdapter typeAd = (MultiSelectAdapter) ((RecyclerView) view.findViewById(R.id.eventTypeFilter)
                .findViewById(R.id.options)).getAdapter();

        List<String> selectedCities = cityAd != null ? cityAd.getSelectedItems() : new ArrayList<>();
        List<String> selectedEventTypes = typeAd != null ? typeAd.getSelectedItems() : new ArrayList<>();


        Integer selectedRating = getSpinnerIntOrNull(ratingSpinner);
        String selectedSortBy = getSpinnerValueOrNull(sortSpinner);
        String selectedSortDir = getSpinnerValueOrNull(sortDirSpinner);

        filterViewModel.setSelectedCities(selectedCities);
        filterViewModel.setSelectedEventTypes(selectedEventTypes);
        filterViewModel.setSelectedRating(selectedRating);

        filterViewModel.setSelectedSortOptions(selectedSortBy);

        filterViewModel.setSortDir(selectedSortDir);
        filterViewModel.setMinDate(startDateText.getText().toString());
        filterViewModel.setMaxDate(endDateText.getText().toString());

        Log.d("EventFilterFragment", "Selected Cities: " + selectedCities);
        Log.d("EventFilterFragment", "Selected EventTypes: " + selectedEventTypes);
        Log.d("EventFilterFragment", "Selected Rating: " + selectedRating);
        Log.d("EventFilterFragment", "Selected SortBy: " + selectedSortBy);
        Log.d("EventFilterFragment", "Selected SortDir: " + selectedSortDir);
        Log.d("EventFilterFragment", "Start Date: " + startDateText.getText());
        Log.d("EventFilterFragment", "End Date: " + endDateText.getText());

        if (isAdded()) {
            Toast.makeText(getContext(), "Filters applied: " + selectedCities.size() + " cities", Toast.LENGTH_SHORT).show();
        }

        filterViewModel.applyNow();

        dismiss();
    }

    private Integer getSpinnerIntOrNull(Spinner sp) {
        Object val = sp.getSelectedItem();
        if (val == null) return null;
        String s = String.valueOf(val);
        if ("Rating".equalsIgnoreCase(s)) return null;
        try { return Integer.parseInt(s); } catch (Exception e) { return null; }
    }

    private String getSpinnerValueOrNull(Spinner sp) {
        Object val = sp.getSelectedItem();
        if (val == null) return null;
        String s = String.valueOf(val);
        if ("Sort by".equalsIgnoreCase(s) || "Direction".equalsIgnoreCase(s)) return null;
        return s;
    }

    private void setUpExistingFilters() {
        // Multi-select
        setupFilter(view, R.id.cityFilter, "Location", cityOptions, filterViewModel.getSelectedCities().getValue());
        setupFilter(view, R.id.eventTypeFilter, "Event Type", eventTypeOptions, filterViewModel.getSelectedEventTypes().getValue());

        // Rating spinner (hint "Rating")
        List<String> ratingData = new ArrayList<>();
        ratingData.add("Rating");             // hint
        if (ratingOptions.isEmpty()) {
            // fallback 1..5
            for (int i = 1; i <= 5; i++) ratingData.add(String.valueOf(i));
        } else {
            ratingData.addAll(ratingOptions);
        }
        setupSpinnerWithHint(ratingSpinner, ratingData);
        if (filterViewModel.getSelectedRating().getValue() != null) {
            int pos = ratingData.indexOf(String.valueOf(filterViewModel.getSelectedRating().getValue()));
            ratingSpinner.setSelection(pos >= 0 ? pos : 0);
        } else {
            ratingSpinner.setSelection(0);
        }

        // SortBy spinner (hint "Sort by")
        List<String> sortData = new ArrayList<>();
        sortData.add("Sort by");              // hint
        sortData.addAll(sortOptions);
        setupSpinnerWithHint(sortSpinner, sortData);
        if (filterViewModel.getSelectedSortOptions().getValue() != null &&
                !filterViewModel.getSelectedSortOptions().getValue().isEmpty()) {
            int pos = sortData.indexOf(filterViewModel.getSelectedSortOptions().getValue());
            sortSpinner.setSelection(pos >= 0 ? pos : 0);
        } else {
            sortSpinner.setSelection(0);
        }

        // SortDir spinner (hint "Direction")
        List<String> dirData = new ArrayList<>();
        dirData.add("Direction");
        dirData.add("ASC");
        dirData.add("DESC");
        setupSpinnerWithHint(sortDirSpinner, dirData);
        if (filterViewModel.getSortDir().getValue() != null) {
            int pos = dirData.indexOf(filterViewModel.getSortDir().getValue());
            sortDirSpinner.setSelection(pos >= 0 ? pos : 0);
        } else {
            sortDirSpinner.setSelection(0);
        }

        // Dates
        startDateText.setText(filterViewModel.getMinDate().getValue() != null ? filterViewModel.getMinDate().getValue() : "");
        endDateText.setText(filterViewModel.getMaxDate().getValue() != null ? filterViewModel.getMaxDate().getValue() : "");
    }

    private void setupSpinnerWithHint(Spinner spinner, List<String> dataWithHintFirst) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(requireContext(),
                android.R.layout.simple_spinner_item, dataWithHintFirst) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0; // disable hint
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(0);
    }

    private void setupFilter(View parentView, int filterId, String filterName, List<String> options, List<String> selectedItems) {
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
        MultiSelectAdapter adapter = new MultiSelectAdapter(options); // multi-select samo ovde
        recyclerView.setAdapter(adapter);

        if (selectedItems != null) adapter.setSelectedItems(selectedItems);

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
                    if (filters.get("sortOptions") instanceof List)
                        sortOptions.addAll((List<String>) filters.get("sortOptions"));

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
    }
}