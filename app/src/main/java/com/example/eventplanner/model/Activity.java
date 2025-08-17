package com.example.eventplanner.model;

public class Activity {
    private String time;
    private String name;
    private String description;
    private String location;
    private boolean isExpanded;

    public Activity(String time, String name, String description, String location) {
        this.time = time;
        this.name = name;
        this.description = description;
        this.location = location;
        this.isExpanded = false;
    }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public boolean isExpanded() { return isExpanded; }
    public void setExpanded(boolean expanded) { this.isExpanded = expanded; }
}
