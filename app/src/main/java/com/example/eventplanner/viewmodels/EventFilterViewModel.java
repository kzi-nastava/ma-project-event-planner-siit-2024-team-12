package com.example.eventplanner.viewmodels;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventFilterViewModel extends ViewModel {

    private final MutableLiveData<List<String>> selectedCities = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<String>> selectedEventTypes = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Integer> selectedRating = new MutableLiveData<>(null); // nullable
    private final MutableLiveData<String> selectedSortOptions = new MutableLiveData<>(null);
    private final MutableLiveData<String> sortDir = new MutableLiveData<>(null); // "ASC"/"DESC"
    private final MutableLiveData<String> minDate = new MutableLiveData<>("");
    private final MutableLiveData<String> maxDate = new MutableLiveData<>("");

    private final MutableLiveData<FilterPayload> appliedFilters = new MutableLiveData<>();

    private final MutableLiveData<Boolean> ignoreCityFilter = new MutableLiveData<>(false);

    private final MutableLiveData<String> searchQuery = new MutableLiveData<>("");
    private final MutableLiveData<Boolean> isPrivileged = new MutableLiveData<>(false);

    private final MutableLiveData<Integer> maxGuests = new MutableLiveData<>(null);

    public LiveData<Integer> getMaxGuests() { return maxGuests; }
    public void setMaxGuests(@Nullable Integer value) { maxGuests.setValue(value); }

    public LiveData<Boolean> getIsPrivileged() {
        return isPrivileged;
    }

    public void setPrivileged(boolean privileged) {
        isPrivileged.setValue(privileged);
    }

    public LiveData<String> getSearchQuery() { return searchQuery; }
    public void setSearchQuery(@Nullable String query) { searchQuery.setValue(query == null ? "" : query); }
    public LiveData<Boolean> getIgnoreCityFilter() { return ignoreCityFilter; }
    public void setIgnoreCityFilter(boolean ignore) { ignoreCityFilter.setValue(ignore); }


    public LiveData<List<String>> getSelectedCities() { return selectedCities; }
    public LiveData<List<String>> getSelectedEventTypes() { return selectedEventTypes; }
    public LiveData<Integer> getSelectedRating() { return selectedRating; }
    public LiveData<String> getSelectedSortOptions() { return selectedSortOptions; }
    public LiveData<String> getSortDir() { return sortDir; }
    public LiveData<String> getMinDate() { return minDate; }
    public LiveData<String> getMaxDate() { return maxDate; }
    public LiveData<FilterPayload> getAppliedFilters() { return appliedFilters; }


    public void setSelectedCities(List<String> cities) {
        selectedCities.setValue(cities != null ? new ArrayList<>(cities) : new ArrayList<>());
    }
    public void setSelectedEventTypes(List<String> types) { selectedEventTypes.setValue(new ArrayList<>(types)); }
    public void setSelectedRating(@Nullable Integer rating) { selectedRating.setValue(rating); } // NULL OK
    public void setSelectedSortOptions(@Nullable String sortBy) { selectedSortOptions.setValue(sortBy); }
    public void setSortDir(@Nullable String dir) { sortDir.setValue(dir); } // "ASC"/"DESC" ili null
    public void setMinDate(String date) { minDate.setValue(date == null ? "" : date); }
    public void setMaxDate(String date) { maxDate.setValue(date == null ? "" : date); }

    public void setSearchQueryAndApply(@Nullable String query) {
        setSearchQuery(query);
        applyNow();
    }

    public void removeCity(String city) {
        List<String> cur = selectedCities.getValue();
        if (cur != null && cur.remove(city)) selectedCities.setValue(new ArrayList<>(cur));
    }

    public void removeEventType(String type) {
        List<String> cur = selectedEventTypes.getValue();
        if (cur != null && cur.remove(type)) selectedEventTypes.setValue(new ArrayList<>(cur));
    }

    public void clearFilters() {
        searchQuery.setValue("");
        selectedCities.setValue(new ArrayList<>());
        selectedEventTypes.setValue(new ArrayList<>());
        selectedRating.setValue(null);
        selectedSortOptions.setValue("");
        sortDir.setValue(null);
        minDate.setValue("");
        maxDate.setValue("");
    }

    public void applyNow() {
        appliedFilters.setValue(buildPayload());
    }

    private FilterPayload buildPayload() {
        return new FilterPayload(
                searchQuery.getValue(),
                selectedCities.getValue() == null ? new ArrayList<>() : selectedCities.getValue(),
                selectedEventTypes.getValue() == null ? new ArrayList<>() : selectedEventTypes.getValue(),
                selectedRating.getValue(),
                selectedSortOptions.getValue(),
                sortDir.getValue(),
                minDate.getValue() == null ? "" : minDate.getValue(),
                maxDate.getValue() == null ? "" : maxDate.getValue(),
                Boolean.TRUE.equals(ignoreCityFilter.getValue()),
                maxGuests.getValue()
        );
    }

    public static class FilterPayload {
        public final List<String> cities;
        public final List<String> eventTypes;
        public final Integer rating; // nullable
        public final String sortBy;
        public final String sortDir;      // "ASC"/"DESC"
        public final String startDate;
        public final String endDate;

        public final boolean ignoreCityFilter;

        public final String searchQuery;

        public final Integer maxGuests;

        public FilterPayload(String searchQuery, List<String> cities, List<String> eventTypes, Integer rating,
                             String sortBy, String sortDir, String startDate, String endDate,  boolean ignoreCityFilter, @Nullable Integer maxGuests) {
            this.searchQuery = searchQuery == null ? "" : searchQuery;
            this.cities = cities;
            this.eventTypes = eventTypes;
            this.rating = rating;
            this.sortBy = sortBy;
            this.sortDir = sortDir;
            this.startDate = startDate;
            this.endDate = endDate;
            this.ignoreCityFilter = ignoreCityFilter;
            this.maxGuests = maxGuests;
        }

        public Map<String, Object> toMap() {
            Map<String, Object> m = new HashMap<>();
            m.put("cities", cities);
            m.put("eventTypes", eventTypes);
            if (rating != null) m.put("rating", rating);
            if (sortBy != null && !sortBy.isEmpty()) m.put("sortBy", sortBy);
            if (sortDir != null && !sortDir.isEmpty()) m.put("sortDir", sortDir);
            if (startDate != null && !startDate.isEmpty()) m.put("startDate", startDate);
            if (endDate != null && !endDate.isEmpty()) m.put("endDate", endDate);
            return m;
        }

        public String getSearchQuery() { return searchQuery; }

        public List<String> getEventTypes() { return eventTypes;
        }

        public List<String> getCities() { return cities;
        }

        public String getStartDate() { return startDate;
        }

        public String getEndDate() {
            return endDate;
        }

        public String getSortBy() {
            return sortBy;
        }

        public String getSortDir() { return sortDir;}

        public Integer getRating() { return rating; }

        public boolean isIgnoreCityFilter() {
            return ignoreCityFilter;
        }
    }

    public void clearAllFilters() {
        selectedCities.setValue(new ArrayList<>());
        selectedEventTypes.setValue(new ArrayList<>());
        selectedRating.setValue(null);
        selectedSortOptions.setValue(null);
        sortDir.setValue(null);
        minDate.setValue("");
        maxDate.setValue("");
        searchQuery.setValue("");

        applyNow();
    }

    public void resetFilters() {
        searchQuery.setValue(null);
        selectedCities.setValue(new ArrayList<>());
        selectedEventTypes.setValue(new ArrayList<>());
        selectedRating.setValue(null);
        selectedSortOptions.setValue(null);
        sortDir.setValue(null);
        minDate.setValue("");
        maxDate.setValue("");
        ignoreCityFilter.setValue(false);
        maxGuests.setValue(null);
        applyNow();
    }



}
