package com.example.eventplanner.dto;


public class LocationDTO {
    private Long id;
    private String name;
    private String city;
    private String country;
    private String address;


    public LocationDTO() {
    }

    public LocationDTO(String name, String address, String city, String country) {
        this.name = name;
        this.address = address;
        this.city = city;
        this.country = country;
    }

    // Getters and Setters

    public Long getId() {return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() { return address;}
    public void setAddress(String address) {this.address = address;}

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public String toString() {
        return "{" +
                "\"name\": \"" + name + "\", " +
                "\"city\": \"" + city + "\"" +
                "\"country\": \"" + country + "\"" +
                "}";
    }
}
