package com.example.eventplanner.dto.event;

import com.example.eventplanner.dto.agenda.CreateActivityDTO;
import com.example.eventplanner.dto.location.CreateLocationDTO;

import java.util.List;

public class CreateEventDTO {
    private String name;
    private String description;
    private String maxGuests;
    private String privacyType;
    private CreateLocationDTO location;
    private String date;
    private String eventTypeName;
    private String organizer;
    private List<CreateActivityDTO> agenda;

    public CreateEventDTO() { super(); }


    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getMaxGuests() { return maxGuests; }
    public void setMaxGuests(String maxGuests) { this.maxGuests = maxGuests; }

    public String getPrivacyType() { return privacyType; }
    public void setPrivacyType(String privacyType) { this.privacyType = privacyType; }

    public CreateLocationDTO getLocation() { return location; }
    public void setLocation(CreateLocationDTO location) { this.location = location; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getEventTypeName() { return eventTypeName; }
    public void setEventType(String eventType) { this.eventTypeName = eventType; }

    public String getOrganizer() { return organizer; }
    public void setOrganizer(String organizer) { this.organizer = organizer; }

    public List<CreateActivityDTO> getAgenda() { return agenda; }
    public void setAgenda(List<CreateActivityDTO> agenda) { this.agenda = agenda; }


    @Override
    public String toString() {
        return "CreateEventDTO{" +
                "name='" + name + '\'' +
                ", maxGuests='" + maxGuests + '\'' +
                ", description='" + description + '\'' +
                ", date='" + date + '\'' +
                ", privacyType='" + privacyType + '\'' +
                '}';
    }

}
