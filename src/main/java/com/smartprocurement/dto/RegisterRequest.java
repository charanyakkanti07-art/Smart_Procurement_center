package com.smartprocurement.dto;

import com.smartprocurement.entity.Role;
import jakarta.validation.constraints.NotBlank;

public class RegisterRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Phone number is required")
    private String phone;

    @NotBlank(message = "Password is required")
    private String password;

    private String language;

    private Role role;

    private Double latitude;

    private Double longitude;

    public RegisterRequest() {
    }

    public RegisterRequest(String name, String phone, String password, String language, Role role, Double latitude, Double longitude) {
        this.name = name;
        this.phone = phone;
        this.password = password;
        this.language = language;
        this.role = role;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
}
