package com.example.eventplanner.dto.event;

import java.time.LocalDate;
import java.time.LocalTime;


public class FavEventDTO {
    private Long id;
    private String name;
    private String description;
    private String city;
    private String country;
    private LocalDate startDate;
    private LocalTime startTime;
    private String imageUrl;

    public FavEventDTO() {}

    public FavEventDTO(Long id, String name, String description, String city, String country, LocalDate startDate,
                       LocalTime startTime, String imageUrl) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.city = city;
        this.country = country;
        this.startDate = startDate;
        this.startTime = startTime;
        this.imageUrl = imageUrl;
    }

    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}

    public String getName() {return name;}
    public void setName(String name) {this.name = name;}

    public String getDescription() {return description;}
    public void setDescription(String description) {this.description = description;}

    public String getCity() {return city;}
    public void setCity(String city) {this.city = city;}

    public String getCountry() {return country;}
    public void setCountry(String country) {this.country = country;}

    public LocalDate getStartDate() {return startDate;}
    public void setStartDate(LocalDate startDate) {this.startDate = startDate;}

    public LocalTime getStartTime() {return startTime;}
    public void setStartTime(LocalTime startTime) {this.startTime = startTime;}

    public String getImageUrl() {return imageUrl;}
    public void setImageUrl(String imageUrl) {this.imageUrl = imageUrl;}

}
