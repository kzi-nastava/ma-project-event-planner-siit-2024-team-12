package com.example.eventplanner.dto.user;

import com.example.eventplanner.enumeration.UserRole;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UpgradeUserDTO {

    private String password;
    private UserRole role;
    private String name;
    private String surname;
    private String address;
    private String phone;

    public UpgradeUserDTO() {
    }

    public UpgradeUserDTO(String password, UserRole role, String name, String surname, String address, String phone) {
        this.password = password;
        this.role = role;
        this.name = name;
        this.surname = surname;
        this.address = address;
        this.phone = phone;
    }

    public UserRole getRole() { return role;}

    public void setRole(UserRole role) { this.role = role;}

    public String getName() { return name;}

    public void setName(String name) { this.name = name; }

    public String getSurname() { return surname; }

    public void setSurname(String surname) { this.surname = surname; }

    public String getAddress() { return address;}

    public void setAddress(String address) { this.address = address;}

    public String getPhone() { return phone;}

    public void setPhone(String phone) { this.phone = phone;}

    public String getPassword() { return password;}

    public void setPassword(String password) { this.password = password; }

}
