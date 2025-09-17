package com.example.eventplanner.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

public class HomeSolutionFilterViewModel extends AndroidViewModel {
    private final MutableLiveData<List<String>> selectedCategories = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<String>> selectedEventTypes = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<String> searchQuery = new MutableLiveData<>();
    private final MutableLiveData<Double> minPrice = new MutableLiveData<>();
    private final MutableLiveData<Double> maxPrice = new MutableLiveData<>();
    private final MutableLiveData<Double> rating = new MutableLiveData<>();
    private final MutableLiveData<String> sortBy = new MutableLiveData<>();
    private final MutableLiveData<String> sortDir = new MutableLiveData<>();
    private final MutableLiveData<String> type = new MutableLiveData<>(); // For "All", "PRODUCT", "SERVICE"
    private final MutableLiveData<Boolean> ignoreCityFilter = new MutableLiveData<>(false);

    private final MutableLiveData<FilterPayload> appliedFilters = new MutableLiveData<>();

    public HomeSolutionFilterViewModel(@NonNull Application application) {
        super(application);
    }


    public LiveData<String> getSearchQuery() { return searchQuery; }
    public void setSearchQuery(String query) { searchQuery.setValue(query); }

    public LiveData<List<String>> getSelectedCategories() { return selectedCategories; }
    public void setSelectedCategories(List<String> categories) { selectedCategories.setValue(categories); }
    public void removeCategory(String category) {
        List<String> updatedCategories = new ArrayList<>(selectedCategories.getValue());
        updatedCategories.remove(category);
        selectedCategories.setValue(updatedCategories);
    }

    public LiveData<List<String>> getSelectedEventTypes() { return selectedEventTypes; }
    public void setSelectedEventTypes(List<String> eventTypes) { selectedEventTypes.setValue(eventTypes); }
    public void removeEventType(String eventType) {
        List<String> updatedEventTypes = new ArrayList<>(selectedEventTypes.getValue());
        updatedEventTypes.remove(eventType);
        selectedEventTypes.setValue(updatedEventTypes);
    }

    public LiveData<Double> getMinPrice() { return minPrice; }
    public void setMinPrice(Double value) { minPrice.setValue(value); }

    public LiveData<Double> getMaxPrice() { return maxPrice; }
    public void setMaxPrice(Double value) { maxPrice.setValue(value); }

    public LiveData<Double> getRating() { return rating; }
    public void setRating(Double value) { rating.setValue(value); }

    public LiveData<String> getSortBy() { return sortBy; }
    public void setSortBy(String value) { sortBy.setValue(value); }

    public LiveData<String> getSortDir() { return sortDir; }
    public void setSortDir(String value) { sortDir.setValue(value); }

    public LiveData<String> getType() { return type; }
    public void setType(String solutionType) { type.setValue(solutionType); }

    public LiveData<Boolean> getIgnoreCityFilter() { return ignoreCityFilter; }
    public void setIgnoreCityFilter(boolean ignore) { ignoreCityFilter.setValue(ignore); }

    public void applyNow() {
        appliedFilters.setValue(new FilterPayload(
                selectedCategories.getValue(),
                selectedEventTypes.getValue(),
                minPrice.getValue(),
                maxPrice.getValue(),
                searchQuery.getValue(),
                rating.getValue(),
                sortBy.getValue(),
                sortDir.getValue(),
                type.getValue(),
                ignoreCityFilter.getValue()
        ));
    }

    public LiveData<FilterPayload> getAppliedFilters() {
        return appliedFilters;
    }

    public static class FilterPayload {
        public final List<String> categories;
        public final List<String> eventTypes;
        public final Double minPrice;
        public final Double maxPrice;
        public final String searchQuery;
        public final Double rating;
        public final String sortBy;
        public final String sortDir;
        public final String type; // "PRODUCT", "SERVICE", null for "All"
        public final boolean ignoreCityFilter;

        public FilterPayload(List<String> categories, List<String> eventTypes, Double minPrice, Double maxPrice, String searchQuery, Double rating, String sortBy, String sortDir, String type, boolean ignoreCityFilter) {
            this.categories = categories != null ? categories : new ArrayList<>();
            this.eventTypes = eventTypes != null ? eventTypes : new ArrayList<>();
            this.minPrice = minPrice;
            this.maxPrice = maxPrice;
            this.searchQuery = searchQuery;
            this.rating = rating;
            this.sortBy = sortBy;
            this.sortDir = sortDir;
            this.type = type;
            this.ignoreCityFilter = ignoreCityFilter;
        }

        public String getSearchQuery() { return searchQuery; }
        public String getType() { return type; }
        public Double getRating() { return rating; }
        public String getSortBy() { return sortBy; }
        public String getSortDir() { return sortDir; }
        public boolean isIgnoreCityFilter() { return ignoreCityFilter; }
    }


    public void resetFilters() {
        selectedCategories.setValue(new ArrayList<>());
        selectedEventTypes.setValue(new ArrayList<>());
        minPrice.setValue(null);
        maxPrice.setValue(null);
        rating.setValue(null);
        sortBy.setValue(null);
        sortDir.setValue(null);
        searchQuery.setValue(null);
        type.setValue(null);
        ignoreCityFilter.setValue(false);
        applyNow();
    }
}