package com.example.eventplanner.model;

import java.time.LocalDate;
import java.util.List;

public class EventFilterData {
    private List<String> cities;
    private List<String> eventTypes;
    private LocalDate minDate;
    private LocalDate maxDate;
    private List<Integer> ratings;
    private List<String> sortOptions;

    public List<String> getCities() { return cities; }
    public List<String> getEventTypes() { return eventTypes; }
    public LocalDate getMinDate() { return minDate; }
    public LocalDate getMaxDate() { return maxDate; }
    public List<String> getSortOptions() { return sortOptions; }
}
