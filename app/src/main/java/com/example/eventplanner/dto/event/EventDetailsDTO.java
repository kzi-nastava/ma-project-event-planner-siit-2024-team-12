package com.example.eventplanner.dto.event;

import com.example.eventplanner.dto.agenda.CreateActivityDTO;
import com.example.eventplanner.dto.location.CreateLocationDTO;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

public class EventDetailsDTO implements Serializable {
    private Long id;
    private String name;
    private String eventType;
    private String maxGuests;
    private String description;
    private CreateLocationDTO location;
    private LocalDate date;
    private List<CreateActivityDTO> activities;

    public EventDetailsDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public String getMaxGuests() { return maxGuests; }
    public void setMaxGuests(String maxGuests) { this.maxGuests = maxGuests; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public CreateLocationDTO getLocation() { return location; }
    public void setLocation(CreateLocationDTO location) { this.location = location; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public List<CreateActivityDTO> getActivities() { return activities; }
    public void setActivities(List<CreateActivityDTO> activities) { this.activities = activities; }

    @Override
    public String toString() {
        return "Details [name=" + name + ", eventType=" + eventType + ", maxGuests=" +
                maxGuests + ", description=" + description + ", location=" + location + ", DATE=" + date +
                ", activities=" + activities + "]";
    }
}
