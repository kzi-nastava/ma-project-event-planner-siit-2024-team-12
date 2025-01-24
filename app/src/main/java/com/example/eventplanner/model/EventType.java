package com.example.eventplanner.model;

import java.util.List;

import okhttp3.internal.annotations.EverythingIsNonNull;

public class EventType {
    private String id;
    private String name;
    private String description;
    private List<String> suggestedCategoryNames;
    private boolean active;
    private boolean isExpanded;

    public EventType() {}

    public EventType(String id, String name, String description, boolean active) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.active = active;
        this.isExpanded = false;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<String> getSuggestedCategoryNames() { return suggestedCategoryNames; }
    public void setSuggestedCategoryNames(List<String> categoryNames) { this.suggestedCategoryNames = categoryNames; }

    public boolean getActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public boolean isExpanded() { return isExpanded; }
    public void setExpanded(boolean expanded) { this.isExpanded = expanded; }

    @Override
    public String toString() {
        return "GetEventTypeDTO{" +
                "id=" + id +
                ", name=" + name + '\'' +
                ", description=" + description + '\'' +
                ", suggested=" + suggestedCategoryNames + '\'' +
                ", active=" + active +
                '}';
    }

}
