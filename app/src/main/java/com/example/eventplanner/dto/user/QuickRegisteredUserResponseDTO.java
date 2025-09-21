package com.example.eventplanner.dto.user;

import com.example.eventplanner.enumeration.UserRole;

public class QuickRegisteredUserResponseDTO {
    private String email;
    private UserRole role;
    private String token;

    public QuickRegisteredUserResponseDTO(String email) {
        this.email = email;
        this.role = UserRole.ROLE_AUTHENTICATED_USER;
    }

    public QuickRegisteredUserResponseDTO(String email, String token) {
        this.email = email;
        this.role = UserRole.ROLE_AUTHENTICATED_USER;
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}