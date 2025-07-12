package com.example.eventplanner.dto.user;


import com.example.eventplanner.dto.LocationDTO;

public class UpdateUserDTO {
    private String name;
    private String surname;
    private LocationDTO location;
    private String phone;
    private String role;
    private Boolean isDeactivated;

    public UpdateUserDTO() {super();}


    public UpdateUserDTO(String name, String surname, LocationDTO location, String phone, Boolean isDeactivated) {
        this.name = name;
        this.surname = surname;
        this.location = location;
        this.phone = phone;
        this.isDeactivated = isDeactivated;
    }



    public String getName() {return name;}
    public void setName(String name) {this.name = name;}

    public String getSurname() {return surname;}
    public void setSurname(String surname) {this.surname = surname;}

    public LocationDTO getLocation() {return location;}
    public void setLocation(LocationDTO location) {this.location = location;}

    public String getPhone() {return phone;}
    public void setPhone(String phone) {this.phone = phone;}

    public Boolean isDeactivated() {return isDeactivated;}
    public void setDeactivated(Boolean isDeactivated) {this.isDeactivated = isDeactivated;}

}

