package com.example.eventplanner.dto.agenda;


import com.example.eventplanner.dto.event.CreateEventDTO;

import java.io.Serializable;
import java.util.Objects;

// activity format sent from front
public class CreateActivityDTO implements Serializable {
    private String time;
    private String name;
    private String description;
    private String location;

    public CreateActivityDTO() {};


    public CreateActivityDTO(String time, String name, String description, String location) {
        this.time = time;
        this.name = name;
        this.description = description;
        this.location = location;
    }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }



    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        CreateActivityDTO that = (CreateActivityDTO) obj;
        return Objects.equals(name, that.name) &&
                Objects.equals(time, that.time) &&
                Objects.equals(description, that.description) &&
                Objects.equals(location, that.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, time, description, location);
    }




    @Override
    public String toString() {
        return "ActivityDTO {time=" + time + ", name=" + name + ", description=" + description + ", location=" + location + "\n}";
    }
}
