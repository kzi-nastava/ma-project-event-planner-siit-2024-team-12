package com.example.eventplanner.model;

public class EventType {
    private String id;
    private String name;
    private String status;
    private boolean isExpanded;

    public EventType(String id, String name, String status) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.isExpanded = false;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public boolean isExpanded() { return isExpanded; }
    public void setExpanded(boolean expanded) { this.isExpanded = expanded; }
}
