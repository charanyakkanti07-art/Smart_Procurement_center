package com.smartprocurement.dto;

import java.util.List;

public class RecommendationDTO {
    private Long centreId;
    private String name;
    private String location;
    private Double distanceKm;
    private Integer travelTimeMinutes;
    private String trafficLevel; // LOW, MODERATE, HIGH
    private boolean trafficAware;
    private String travelTimeSource; // "google" or "fallback"
    private Integer queueLength;
    private Double totalCapacity;
    private Double currentLoad;
    private Double loadPercentage;
    private String loadClassification; // LOW, MODERATE, HIGH, OVERLOADED
    private Double averageProcessingMinutes;
    private Integer estimatedWaitMinutes;
    private Integer estimatedTotalMinutes;
    private String operatingStatus; // ACTIVE, OVERLOADED, CLOSED
    private String availability; // AVAILABLE, BUSY, NEAR_CAPACITY, OVERLOADED, CLOSED
    private Double score;
    private List<String> reasons;
    private List<String> warnings;
    private String recommendationBadge; // RECOMMENDED, ALTERNATIVE, NOT_RECOMMENDED

    public RecommendationDTO() {
    }

    public RecommendationDTO(Long centreId, String name, String location, Double distanceKm, Integer travelTimeMinutes,
                             String trafficLevel, boolean trafficAware, String travelTimeSource, Integer queueLength,
                             Double totalCapacity, Double currentLoad, Double loadPercentage, String loadClassification,
                             Double averageProcessingMinutes, Integer estimatedWaitMinutes, Integer estimatedTotalMinutes,
                             String operatingStatus, String availability, Double score, List<String> reasons,
                             List<String> warnings, String recommendationBadge) {
        this.centreId = centreId;
        this.name = name;
        this.location = location;
        this.distanceKm = distanceKm;
        this.travelTimeMinutes = travelTimeMinutes;
        this.trafficLevel = trafficLevel;
        this.trafficAware = trafficAware;
        this.travelTimeSource = travelTimeSource;
        this.queueLength = queueLength;
        this.totalCapacity = totalCapacity;
        this.currentLoad = currentLoad;
        this.loadPercentage = loadPercentage;
        this.loadClassification = loadClassification;
        this.averageProcessingMinutes = averageProcessingMinutes;
        this.estimatedWaitMinutes = estimatedWaitMinutes;
        this.estimatedTotalMinutes = estimatedTotalMinutes;
        this.operatingStatus = operatingStatus;
        this.availability = availability;
        this.score = score;
        this.reasons = reasons;
        this.warnings = warnings;
        this.recommendationBadge = recommendationBadge;
    }

    public Long getCentreId() { return centreId; }
    public void setCentreId(Long centreId) { this.centreId = centreId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(Double distanceKm) { this.distanceKm = distanceKm; }

    public Integer getTravelTimeMinutes() { return travelTimeMinutes; }
    public void setTravelTimeMinutes(Integer travelTimeMinutes) { this.travelTimeMinutes = travelTimeMinutes; }

    public String getTrafficLevel() { return trafficLevel; }
    public void setTrafficLevel(String trafficLevel) { this.trafficLevel = trafficLevel; }

    public boolean isTrafficAware() { return trafficAware; }
    public void setTrafficAware(boolean trafficAware) { this.trafficAware = trafficAware; }

    public String getTravelTimeSource() { return travelTimeSource; }
    public void setTravelTimeSource(String travelTimeSource) { this.travelTimeSource = travelTimeSource; }

    public Integer getQueueLength() { return queueLength; }
    public void setQueueLength(Integer queueLength) { this.queueLength = queueLength; }

    public Double getTotalCapacity() { return totalCapacity; }
    public void setTotalCapacity(Double totalCapacity) { this.totalCapacity = totalCapacity; }

    public Double getCurrentLoad() { return currentLoad; }
    public void setCurrentLoad(Double currentLoad) { this.currentLoad = currentLoad; }

    public Double getLoadPercentage() { return loadPercentage; }
    public void setLoadPercentage(Double loadPercentage) { this.loadPercentage = loadPercentage; }

    public String getLoadClassification() { return loadClassification; }
    public void setLoadClassification(String loadClassification) { this.loadClassification = loadClassification; }

    public Double getAverageProcessingMinutes() { return averageProcessingMinutes; }
    public void setAverageProcessingMinutes(Double averageProcessingMinutes) { this.averageProcessingMinutes = averageProcessingMinutes; }

    public Integer getEstimatedWaitMinutes() { return estimatedWaitMinutes; }
    public void setEstimatedWaitMinutes(Integer estimatedWaitMinutes) { this.estimatedWaitMinutes = estimatedWaitMinutes; }

    public Integer getEstimatedTotalMinutes() { return estimatedTotalMinutes; }
    public void setEstimatedTotalMinutes(Integer estimatedTotalMinutes) { this.estimatedTotalMinutes = estimatedTotalMinutes; }

    public String getOperatingStatus() { return operatingStatus; }
    public void setOperatingStatus(String operatingStatus) { this.operatingStatus = operatingStatus; }

    public String getAvailability() { return availability; }
    public void setAvailability(String availability) { this.availability = availability; }

    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }

    public List<String> getReasons() { return reasons; }
    public void setReasons(List<String> reasons) { this.reasons = reasons; }

    public List<String> getWarnings() { return warnings; }
    public void setWarnings(List<String> warnings) { this.warnings = warnings; }

    public String getRecommendationBadge() { return recommendationBadge; }
    public void setRecommendationBadge(String recommendationBadge) { this.recommendationBadge = recommendationBadge; }

    public static RecommendationDTOBuilder builder() {
        return new RecommendationDTOBuilder();
    }

    public static class RecommendationDTOBuilder {
        private Long centreId;
        private String name;
        private String location;
        private Double distanceKm;
        private Integer travelTimeMinutes;
        private String trafficLevel;
        private boolean trafficAware;
        private String travelTimeSource;
        private Integer queueLength;
        private Double totalCapacity;
        private Double currentLoad;
        private Double loadPercentage;
        private String loadClassification;
        private Double averageProcessingMinutes;
        private Integer estimatedWaitMinutes;
        private Integer estimatedTotalMinutes;
        private String operatingStatus;
        private String availability;
        private Double score;
        private List<String> reasons;
        private List<String> warnings;
        private String recommendationBadge;

        public RecommendationDTOBuilder centreId(Long centreId) { this.centreId = centreId; return this; }
        public RecommendationDTOBuilder name(String name) { this.name = name; return this; }
        public RecommendationDTOBuilder location(String location) { this.location = location; return this; }
        public RecommendationDTOBuilder distanceKm(Double distanceKm) { this.distanceKm = distanceKm; return this; }
        public RecommendationDTOBuilder travelTimeMinutes(Integer travelTimeMinutes) { this.travelTimeMinutes = travelTimeMinutes; return this; }
        public RecommendationDTOBuilder trafficLevel(String trafficLevel) { this.trafficLevel = trafficLevel; return this; }
        public RecommendationDTOBuilder trafficAware(boolean trafficAware) { this.trafficAware = trafficAware; return this; }
        public RecommendationDTOBuilder travelTimeSource(String travelTimeSource) { this.travelTimeSource = travelTimeSource; return this; }
        public RecommendationDTOBuilder queueLength(Integer queueLength) { this.queueLength = queueLength; return this; }
        public RecommendationDTOBuilder totalCapacity(Double totalCapacity) { this.totalCapacity = totalCapacity; return this; }
        public RecommendationDTOBuilder currentLoad(Double currentLoad) { this.currentLoad = currentLoad; return this; }
        public RecommendationDTOBuilder loadPercentage(Double loadPercentage) { this.loadPercentage = loadPercentage; return this; }
        public RecommendationDTOBuilder loadClassification(String loadClassification) { this.loadClassification = loadClassification; return this; }
        public RecommendationDTOBuilder averageProcessingMinutes(Double averageProcessingMinutes) { this.averageProcessingMinutes = averageProcessingMinutes; return this; }
        public RecommendationDTOBuilder estimatedWaitMinutes(Integer estimatedWaitMinutes) { this.estimatedWaitMinutes = estimatedWaitMinutes; return this; }
        public RecommendationDTOBuilder estimatedTotalMinutes(Integer estimatedTotalMinutes) { this.estimatedTotalMinutes = estimatedTotalMinutes; return this; }
        public RecommendationDTOBuilder operatingStatus(String operatingStatus) { this.operatingStatus = operatingStatus; return this; }
        public RecommendationDTOBuilder availability(String availability) { this.availability = availability; return this; }
        public RecommendationDTOBuilder score(Double score) { this.score = score; return this; }
        public RecommendationDTOBuilder reasons(List<String> reasons) { this.reasons = reasons; return this; }
        public RecommendationDTOBuilder warnings(List<String> warnings) { this.warnings = warnings; return this; }
        public RecommendationDTOBuilder recommendationBadge(String recommendationBadge) { this.recommendationBadge = recommendationBadge; return this; }

        public RecommendationDTO build() {
            return new RecommendationDTO(centreId, name, location, distanceKm, travelTimeMinutes, trafficLevel, trafficAware,
                    travelTimeSource, queueLength, totalCapacity, currentLoad, loadPercentage, loadClassification,
                    averageProcessingMinutes, estimatedWaitMinutes, estimatedTotalMinutes, operatingStatus, availability,
                    score, reasons, warnings, recommendationBadge);
        }
    }
}
