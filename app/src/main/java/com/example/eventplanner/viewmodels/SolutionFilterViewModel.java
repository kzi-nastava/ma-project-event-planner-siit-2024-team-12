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
        selectedCategories.setValue(categories);
    }

    public void setSelectedEventTypes(List<String> eventTypes) {
        selectedEventTypes.setValue(eventTypes);
    }

    public void setSelectedAvailability(List<String> availability) {
        selectedAvailability.setValue(availability);
    }

    public void setSelectedDescriptions(List<String> descriptions) {
        selectedDescriptions.setValue(descriptions);
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
}
