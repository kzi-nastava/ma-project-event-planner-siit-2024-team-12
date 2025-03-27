package com.example.eventplanner.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;


import java.util.ArrayList;
import java.util.List;

public class SolutionFilterViewModel extends AndroidViewModel {
    private final MutableLiveData<List<String>> selectedCategories = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<String>> selectedEventTypes = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<String>> selectedAvailability = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<String>> selectedDescriptions = new MutableLiveData<>(new ArrayList<>());

    public SolutionFilterViewModel(@NonNull Application application) {
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

}
