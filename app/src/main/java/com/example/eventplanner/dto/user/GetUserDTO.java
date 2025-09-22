package com.example.eventplanner.dto.user;

import com.example.eventplanner.dto.LocationDTO;

import java.util.HashSet;
import java.util.Set;

public class GetUserDTO {
    private Long id;
    private String email;
    private String name;
    private String surname;
    private LocationDTO location;
    private String phone;
    private String role;
    private String imageUrl;
    private Set<Long> blockedUsersIds = new HashSet<>();


    public GetUserDTO() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getImageUrl() {return imageUrl;}

    public void setImageUrl(String imageUrl) {this.imageUrl = imageUrl;}
    public Set<Long> getBlockedUsersIds() { return blockedUsersIds; }
    public void setBlockedUsersIds(Set<Long> blockedUsersIds) { this.blockedUsersIds = blockedUsersIds; }
}

