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
    private final MutableLiveData<List<String>> selectedAvailability = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<String>> selectedDescriptions = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Double> minPrice = new MutableLiveData<>();
    private final MutableLiveData<Double> maxPrice = new MutableLiveData<>();

    private final MutableLiveData<Boolean> ignoreCityFilter = new MutableLiveData<>(false);

    private final MutableLiveData<FilterPayload> appliedFilters = new MutableLiveData<>();

    public LiveData<Boolean> getIgnoreCityFilter() { return ignoreCityFilter; }
    public void setIgnoreCityFilter(boolean ignore) { ignoreCityFilter.setValue(ignore); }

    public HomeSolutionFilterViewModel(@NonNull Application application) {
        super(application);
    }

    public void setSelectedCategories(List<String> categories) {
        List<String> currentCategories = selectedCategories.getValue();
        for (String category : categories) {
            if (!currentCategories.contains(category)) {
                currentCategories.add(category);
            }
        }
        selectedCategories.setValue(currentCategories);
    }


    public void setSelectedEventTypes(List<String> eventTypes) {
        List<String> currentEventTypes = selectedEventTypes.getValue();
        for (String eventType : eventTypes) {
            if (!currentEventTypes.contains(eventType)) {
                currentEventTypes.add(eventType);
            }
        }
        selectedEventTypes.setValue(currentEventTypes);
    }

    public void setSelectedAvailability(List<String> availability) {
        List<String> currentAvailability = selectedAvailability.getValue();
        for (String availabilityOption : availability) {
            if (!currentAvailability.contains(availabilityOption)) {
                currentAvailability.add(availabilityOption);
            }
        }
        selectedAvailability.setValue(currentAvailability);
    }

    public void setSelectedDescriptions(List<String> descriptions) {
        List<String> currentDescriptions = selectedDescriptions.getValue();
        for (String description : descriptions) {
            if (!currentDescriptions.contains(description)) {
                currentDescriptions.add(description);
            }
        }
        selectedDescriptions.setValue(currentDescriptions);
    }


    public LiveData<List<String>> getSelectedCategories() {
        return selectedCategories;
    }

    public LiveData<List<String>> getSelectedEventTypes() {
        return selectedEventTypes;
    }

    public LiveData<List<String>> getSelectedAvailability() {
        return selectedAvailability;
    }

    public LiveData<List<String>> getSelectedDescriptions() {
        return selectedDescriptions;
    }


    public void removeCategory(String category) {
        List<String> updatedCategories = new ArrayList<>(getSelectedCategories().getValue());
        updatedCategories.remove(category);
        selectedCategories.setValue(updatedCategories);
    }


    public void removeEventType(String eventType) {
        List<String> updatedEventTypes = new ArrayList<>(getSelectedEventTypes().getValue());
        updatedEventTypes.remove(eventType);
        selectedEventTypes.setValue(updatedEventTypes);
    }

    public void removeAvailability(String availability) {
        List<String> updatedAvailability = new ArrayList<>(getSelectedAvailability().getValue());
        updatedAvailability.remove(availability);
        selectedAvailability.setValue(updatedAvailability);
    }

    public void removeDescription(String description) {
        List<String> updatedDescriptions = new ArrayList<>(getSelectedDescriptions().getValue());
        updatedDescriptions.remove(description);
        selectedDescriptions.setValue(updatedDescriptions);
    }



    public LiveData<Double> getMinPrice() {
        return minPrice;
    }

    public LiveData<Double> getMaxPrice() {
        return maxPrice;
    }

    public void setMinPrice(Double value) {
        minPrice.setValue(value);
    }

    public void setMaxPrice(Double value) {
        maxPrice.setValue(value);
    }

    public void applyNow() {
        appliedFilters.setValue(new FilterPayload(
                selectedCategories.getValue(),
                selectedEventTypes.getValue(),
                selectedAvailability.getValue(),
                selectedDescriptions.getValue(),
                minPrice.getValue(),
                maxPrice.getValue()
        ));
    }

    public LiveData<FilterPayload> getAppliedFilters() {
        return appliedFilters;
    }

    public static class FilterPayload {
        public final List<String> categories;
        public final List<String> eventTypes;
        public final List<String> availability;
        public final List<String> descriptions;
        public final Double minPrice;
        public final Double maxPrice;

        public FilterPayload(List<String> categories, List<String> eventTypes, List<String> availability, List<String> descriptions, Double minPrice, Double maxPrice) {
            this.categories = categories;
            this.eventTypes = eventTypes;
            this.availability = availability;
            this.descriptions = descriptions;
            this.minPrice = minPrice;
            this.maxPrice = maxPrice;
        }
    }

}
