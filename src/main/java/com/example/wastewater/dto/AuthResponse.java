package com.example.wastewater.dto;

import com.example.wastewater.enums.UserRole;

public class AuthResponse {
    private String username;
    private String fullName;
    private UserRole role;
    private String message;

    public AuthResponse() {}

    public AuthResponse(String username, String fullName, UserRole role, String message) {
        this.username = username;
        this.fullName = fullName;
        this.role = role;
        this.message = message;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
