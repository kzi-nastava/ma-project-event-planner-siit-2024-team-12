package com.example.eventplanner.dto.user;

import com.example.eventplanner.enumeration.UserRole;

public class QuickRegisteredUserDTO {
    private String password;
    private UserRole role;

    public QuickRegisteredUserDTO(String password) {
        this.password = password;
        this.role = UserRole.ROLE_AUTHENTICATED_USER;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }
}
