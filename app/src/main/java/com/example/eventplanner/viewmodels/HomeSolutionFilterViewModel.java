package com.example.eventplanner.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

public class HomeSolutionFilterViewModel extends AndroidViewModel {
    private final MutableLiveData<String> selectedCategory = new MutableLiveData<>();
    private final MutableLiveData<String> selectedEventType = new MutableLiveData<>();
    private final MutableLiveData<String> searchQuery = new MutableLiveData<>();
    private final MutableLiveData<Double> minPrice = new MutableLiveData<>();
    private final MutableLiveData<Double> maxPrice = new MutableLiveData<>();
    private final MutableLiveData<Double> minDiscount = new MutableLiveData<>();
    private final MutableLiveData<Double> maxDiscount = new MutableLiveData<>();
    private final MutableLiveData<Double> rating = new MutableLiveData<>();
    private final MutableLiveData<String> sortBy = new MutableLiveData<>();
    private final MutableLiveData<String> sortDir = new MutableLiveData<>();
    private final MutableLiveData<String> type = new MutableLiveData<>();
    private final MutableLiveData<Boolean> ignoreCityFilter = new MutableLiveData<>(false);

    private final MutableLiveData<FilterPayload> appliedFilters = new MutableLiveData<>();

    public HomeSolutionFilterViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<Double> getMinDiscount() { return minDiscount; }
    public void setMinDiscount(Double value) { minDiscount.setValue(value); }

    public LiveData<Double> getMaxDiscount() { return maxDiscount; }
    public void setMaxDiscount(Double value) { maxDiscount.setValue(value); }

    public LiveData<String> getSearchQuery() { return searchQuery; }
    public void setSearchQuery(String query) { searchQuery.setValue(query); }

    public LiveData<String> getSelectedCategory() { return selectedCategory; }
    public void setSelectedCategory(String category) { selectedCategory.setValue(category); }
    public void removeCategory(String category) {
        String updatedCategories = selectedCategory.getValue();

        selectedCategory.setValue(updatedCategories);
    }

    public LiveData<String> getSelectedEventType() { return selectedEventType; }
    public void setSelectedEventType(String eventType) { selectedEventType.setValue(eventType); }
    public void removeEventType(String eventType) {
        String updatedEventTypes = selectedEventType.getValue();

        selectedEventType.setValue(updatedEventTypes);
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
                selectedCategory.getValue(),
                selectedEventType.getValue(),
                minPrice.getValue(),
                maxPrice.getValue(),
                minDiscount.getValue(),
                maxDiscount.getValue(),
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
        public final String category;
        public final String eventType;
        public final Double minPrice;
        public final Double maxPrice;
        public final Double minDiscount;
        public final Double maxDiscount;
        public final String searchQuery;
        public final Double rating;
        public final String sortBy;
        public final String sortDir;
        public final String type;
        public final boolean ignoreCityFilter;

        public FilterPayload(String category, String eventType, Double minPrice, Double maxPrice, Double minDiscount, Double maxDiscount, String searchQuery, Double rating, String sortBy, String sortDir, String type, boolean ignoreCityFilter) {
            this.category = category;
            this.eventType = eventType;
            this.minPrice = minPrice;
            this.maxPrice = maxPrice;
            this.minDiscount = minDiscount;
            this.maxDiscount = maxDiscount;
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
        selectedCategory.setValue(null);
        selectedEventType.setValue(null);
        minPrice.setValue(null);
        maxPrice.setValue(null);
        minDiscount.setValue(null);
        maxDiscount.setValue(null);
        rating.setValue(null);
        sortBy.setValue(null);
        sortDir.setValue(null);
        searchQuery.setValue(null);
        type.setValue(null);
        ignoreCityFilter.setValue(false);
        applyNow();
    }
}