package com.example.eventplanner.dto.event;

import com.example.eventplanner.activities.homepage.CardItem;
import com.google.gson.annotations.SerializedName;

import java.time.LocalDate;

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
    private LocalDate date;

    @SerializedName("eventTypeName")
    private String eventTypeName;

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getImageUrl() { return imageUrl; }

}
