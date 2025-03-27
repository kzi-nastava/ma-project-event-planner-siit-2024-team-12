package com.example.eventplanner.dto.solution;


import java.util.List;

public class SolutionFilterParams {
    private List<String> categories;
    private List<String> eventTypes;
    private Double minPrice;
    private Double maxPrice;
    private List<Boolean> isAvailable;
    private List<String> descriptions;

    public SolutionFilterParams() {}

    public List<String> getCategories() { return categories; }
    public void setCategories(List<String> categories) { this.categories = categories; }

    public List<String> getEventTypes() { return eventTypes; }
    public void setEventTypes(List<String> eventTypes) { this.eventTypes = eventTypes; }

    public Double getMinPrice() { return minPrice; }
    public void setMinPrice(Double minPrice) { this.minPrice = minPrice; }

    public Double getMaxPrice() { return maxPrice; }
    public void setMaxPrice(Double maxPrice) { this.maxPrice = maxPrice; }

    public List<Boolean> getIsAvailable() { return isAvailable; }
    public void setIsAvailable(List<Boolean> isAvailable) { this.isAvailable = isAvailable; }

    public List<String> getDescriptions() { return descriptions; }
    public void setDescriptions(List<String> descriptions) { this.descriptions = descriptions; }

    @Override
    public String toString() {
        return "SolutionFilterParams [categories=" + categories + ", eventTypes=" + eventTypes + ", minPrice=" +
                minPrice + ", maxPrice=" + maxPrice + ", isAvailable=" + isAvailable + ", descriptions=" +
                descriptions + "]";
    }
}

