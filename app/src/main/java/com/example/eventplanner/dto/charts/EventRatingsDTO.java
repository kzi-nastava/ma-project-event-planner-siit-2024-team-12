package com.example.eventplanner.dto.charts;

import java.util.Map;

public class EventRatingsDTO {
    private String eventName;
    private Double averageRating;
    private Map<Integer, Integer> ratingCounts;

    public EventRatingsDTO(String eventName, Double averageRating, Map<Integer, Integer> ratingCounts) {
        this.eventName = eventName;
        this.averageRating = averageRating;
        this.ratingCounts = ratingCounts;
    }

    public String getEventName() { return eventName; }
    public void setEventName(String eventName) { this.eventName = eventName; }

    public Double getAverageRating() { return averageRating; }
    public void setAverageRating(Double averageRating) { this.averageRating = averageRating; }

    public Map<Integer, Integer> getRatingCounts() { return ratingCounts; }
    public void setRatingCounts(Map<Integer, Integer> ratingCounts) { this.ratingCounts = ratingCounts; }
}
