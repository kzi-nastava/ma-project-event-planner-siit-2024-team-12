package com.example.eventplanner.dto.user;

import com.example.eventplanner.dto.LocationDTO;
import com.example.eventplanner.enumeration.UserRole;

public class CreatedUserDTO {

    private String email;
    private String password;
    private String name;
    private String surname;
    private LocationDTO location;
    private String phone;
    private UserRole role;
    private Boolean isActivated;
    private Boolean isVerified;

    // Default constructor
    public CreatedUserDTO() {
    }

    // All-args constructor
    public CreatedUserDTO(String email, String password, String name, String surname, LocationDTO location, String phone,
                          UserRole role, Boolean isActivated, Boolean isVerified) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.location = location;
        this.phone = phone;
        this.role = role;
        this.isActivated = isActivated;
        this.isVerified = isVerified;
    }

    // Getters and Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public LocationDTO getLocation() {
        return location;
    }

    public void setLocation(LocationDTO location) {
        this.location = location;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public Boolean getIsActivated() {
        return isActivated;
    }

    public void setIsActivated(Boolean isActivated) {
        this.isActivated = isActivated;
    }

    public Boolean getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(Boolean isVerified) {
        this.isVerified = isVerified;
    }
}