package com.example.eventplanner.model;

import okhttp3.internal.annotations.EverythingIsNonNull;

public class EventType {
    private String id;
    private String name;
    private boolean active;
    private boolean isExpanded;

    public EventType() {}

    public EventType(String id, String name, boolean active) {
        this.id = id;
        this.name = name;
        this.active = active;
        this.isExpanded = false;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public boolean getActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public boolean isExpanded() { return isExpanded; }
    public void setExpanded(boolean expanded) { this.isExpanded = expanded; }

    @Override
    public String toString() {
        return "GetEventTypeDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", active=" + active +
                '}';
    }

}
