package com.example.eventplanner.dto.event;

import com.example.eventplanner.activities.homepage.CardItem;
import com.example.eventplanner.dto.LocationDTO;
import com.google.gson.annotations.SerializedName;

import java.time.LocalDate;
import java.util.Date;

public class GetEventDTO implements CardItem {

    @SerializedName("id")
    private Long id;

    @SerializedName("name")
    private String name;

    @SerializedName("imageUrl")
    private String imageUrl;

    @SerializedName("description")
    private String description;

    @SerializedName("date")
    private Date date;

    private LocationDTO location;

    @SerializedName("eventTypeName")
    private String eventTypeName;

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getImageUrl() { return imageUrl; }
    public String getDescription() { return description; }
    public String getEventTypeName() { return eventTypeName; }
    public Date getDate() { return date; }
    public LocationDTO getLocation() { return location; }

}
