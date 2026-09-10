package com.smartprocurement.dto;

import java.time.LocalDateTime;

public class FarmerDTO {
    private Long farmerId;
    private String name;
    private String phone;
    private String language;
    private Double latitude;
    private Double longitude;
    private LocalDateTime createdAt;

    public FarmerDTO() {
    }

    public FarmerDTO(Long farmerId, String name, String phone, String language, Double latitude, Double longitude, LocalDateTime createdAt) {
        this.farmerId = farmerId;
        this.name = name;
        this.phone = phone;
        this.language = language;
        this.latitude = latitude;
        this.longitude = longitude;
        this.createdAt = createdAt;
    }

    public Long getFarmerId() { return farmerId; }
    public void setFarmerId(Long farmerId) { this.farmerId = farmerId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static FarmerDTOBuilder builder() {
        return new FarmerDTOBuilder();
    }

    public static class FarmerDTOBuilder {
        private Long farmerId;
        private String name;
        private String phone;
        private String language;
        private Double latitude;
        private Double longitude;
        private LocalDateTime createdAt;

        public FarmerDTOBuilder farmerId(Long farmerId) { this.farmerId = farmerId; return this; }
        public FarmerDTOBuilder name(String name) { this.name = name; return this; }
        public FarmerDTOBuilder phone(String phone) { this.phone = phone; return this; }
        public FarmerDTOBuilder language(String language) { this.language = language; return this; }
        public FarmerDTOBuilder latitude(Double latitude) { this.latitude = latitude; return this; }
        public FarmerDTOBuilder longitude(Double longitude) { this.longitude = longitude; return this; }
        public FarmerDTOBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public FarmerDTO build() {
            return new FarmerDTO(farmerId, name, phone, language, latitude, longitude, createdAt);
        }
    }
}
