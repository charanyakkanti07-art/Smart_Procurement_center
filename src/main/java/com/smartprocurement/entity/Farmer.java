package com.smartprocurement.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "farmers")
public class Farmer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long farmerId;

    private String name;

    @Column(unique = true, nullable = false)
    private String phone;

    private String password;

    private String language;

    private Double latitude;

    private Double longitude;

    private LocalDateTime createdAt;

    public Farmer() {
    }

    public Farmer(Long farmerId, String name, String phone, String password, String language, Double latitude, Double longitude, LocalDateTime createdAt) {
        this.farmerId = farmerId;
        this.name = name;
        this.phone = phone;
        this.password = password;
        this.language = language;
        this.latitude = latitude;
        this.longitude = longitude;
        this.createdAt = createdAt;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getFarmerId() { return farmerId; }
    public void setFarmerId(Long farmerId) { this.farmerId = farmerId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static FarmerBuilder builder() {
        return new FarmerBuilder();
    }

    public static class FarmerBuilder {
        private Long farmerId;
        private String name;
        private String phone;
        private String password;
        private String language;
        private Double latitude;
        private Double longitude;
        private LocalDateTime createdAt;

        public FarmerBuilder farmerId(Long farmerId) { this.farmerId = farmerId; return this; }
        public FarmerBuilder name(String name) { this.name = name; return this; }
        public FarmerBuilder phone(String phone) { this.phone = phone; return this; }
        public FarmerBuilder password(String password) { this.password = password; return this; }
        public FarmerBuilder language(String language) { this.language = language; return this; }
        public FarmerBuilder latitude(Double latitude) { this.latitude = latitude; return this; }
        public FarmerBuilder longitude(Double longitude) { this.longitude = longitude; return this; }
        public FarmerBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public Farmer build() {
            return new Farmer(farmerId, name, phone, password, language, latitude, longitude, createdAt);
        }
    }
}
