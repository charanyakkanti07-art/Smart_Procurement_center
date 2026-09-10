package com.smartprocurement.dto;

import com.smartprocurement.entity.CentreStatus;

public class CentreDTO {
    private Long centreId;
    private String name;
    private String location;
    private Double latitude;
    private Double longitude;
    private Double totalCapacity;
    private Double currentLoad;
    private Double availableCapacity;
    private CentreStatus status;

    public CentreDTO() {
    }

    public CentreDTO(Long centreId, String name, String location, Double latitude, Double longitude, Double totalCapacity, Double currentLoad, Double availableCapacity, CentreStatus status) {
        this.centreId = centreId;
        this.name = name;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.totalCapacity = totalCapacity;
        this.currentLoad = currentLoad;
        this.availableCapacity = availableCapacity;
        this.status = status;
    }

    public Long getCentreId() { return centreId; }
    public void setCentreId(Long centreId) { this.centreId = centreId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public Double getTotalCapacity() { return totalCapacity; }
    public void setTotalCapacity(Double totalCapacity) { this.totalCapacity = totalCapacity; }

    public Double getCurrentLoad() { return currentLoad; }
    public void setCurrentLoad(Double currentLoad) { this.currentLoad = currentLoad; }

    public Double getAvailableCapacity() { return availableCapacity; }
    public void setAvailableCapacity(Double availableCapacity) { this.availableCapacity = availableCapacity; }

    public CentreStatus getStatus() { return status; }
    public void setStatus(CentreStatus status) { this.status = status; }

    public static CentreDTOBuilder builder() {
        return new CentreDTOBuilder();
    }

    public static class CentreDTOBuilder {
        private Long centreId;
        private String name;
        private String location;
        private Double latitude;
        private Double longitude;
        private Double totalCapacity;
        private Double currentLoad;
        private Double availableCapacity;
        private CentreStatus status;

        public CentreDTOBuilder centreId(Long centreId) { this.centreId = centreId; return this; }
        public CentreDTOBuilder name(String name) { this.name = name; return this; }
        public CentreDTOBuilder location(String location) { this.location = location; return this; }
        public CentreDTOBuilder latitude(Double latitude) { this.latitude = latitude; return this; }
        public CentreDTOBuilder longitude(Double longitude) { this.longitude = longitude; return this; }
        public CentreDTOBuilder totalCapacity(Double totalCapacity) { this.totalCapacity = totalCapacity; return this; }
        public CentreDTOBuilder currentLoad(Double currentLoad) { this.currentLoad = currentLoad; return this; }
        public CentreDTOBuilder availableCapacity(Double availableCapacity) { this.availableCapacity = availableCapacity; return this; }
        public CentreDTOBuilder status(CentreStatus status) { this.status = status; return this; }

        public CentreDTO build() {
            return new CentreDTO(centreId, name, location, latitude, longitude, totalCapacity, currentLoad, availableCapacity, status);
        }
    }
}
