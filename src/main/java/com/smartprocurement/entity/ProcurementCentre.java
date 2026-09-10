package com.smartprocurement.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "procurement_centres")
public class ProcurementCentre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long centreId;

    private String name;

    private String location;

    private Double latitude;

    private Double longitude;

    private Double totalCapacity;

    @Column(nullable = false)
    private Double currentLoad;

    @Enumerated(EnumType.STRING)
    private CentreStatus status;

    public ProcurementCentre() {
    }

    public ProcurementCentre(Long centreId, String name, String location, Double latitude, Double longitude, Double totalCapacity, Double currentLoad, CentreStatus status) {
        this.centreId = centreId;
        this.name = name;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.totalCapacity = totalCapacity;
        this.currentLoad = currentLoad != null ? currentLoad : 0.0;
        this.status = status != null ? status : CentreStatus.ACTIVE;
    }

    @PrePersist
    protected void init() {
        if (this.currentLoad == null) {
            this.currentLoad = 0.0;
        }
        if (this.status == null) {
            this.status = CentreStatus.ACTIVE;
        }
    }

    public Double getAvailableCapacity() {
        if (totalCapacity == null) return 0.0;
        double current = currentLoad != null ? currentLoad : 0.0;
        return Math.max(0.0, totalCapacity - current);
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

    public CentreStatus getStatus() { return status; }
    public void setStatus(CentreStatus status) { this.status = status; }

    public static ProcurementCentreBuilder builder() {
        return new ProcurementCentreBuilder();
    }

    public static class ProcurementCentreBuilder {
        private Long centreId;
        private String name;
        private String location;
        private Double latitude;
        private Double longitude;
        private Double totalCapacity;
        private Double currentLoad;
        private CentreStatus status;

        public ProcurementCentreBuilder centreId(Long centreId) { this.centreId = centreId; return this; }
        public ProcurementCentreBuilder name(String name) { this.name = name; return this; }
        public ProcurementCentreBuilder location(String location) { this.location = location; return this; }
        public ProcurementCentreBuilder latitude(Double latitude) { this.latitude = latitude; return this; }
        public ProcurementCentreBuilder longitude(Double longitude) { this.longitude = longitude; return this; }
        public ProcurementCentreBuilder totalCapacity(Double totalCapacity) { this.totalCapacity = totalCapacity; return this; }
        public ProcurementCentreBuilder currentLoad(Double currentLoad) { this.currentLoad = currentLoad; return this; }
        public ProcurementCentreBuilder status(CentreStatus status) { this.status = status; return this; }

        public ProcurementCentre build() {
            return new ProcurementCentre(centreId, name, location, latitude, longitude, totalCapacity, currentLoad, status);
        }
    }
}
