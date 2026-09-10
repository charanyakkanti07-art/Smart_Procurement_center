package com.smartprocurement.dto;

public class TravelTimeDataDTO {
    private Double distanceKm;
    private Integer travelTimeMinutes;
    private Integer normalTravelTimeMinutes;
    private Integer trafficDelayMinutes;
    private String trafficStatus; // LOW, MODERATE, HIGH, SEVERE, UNKNOWN, UNAVAILABLE
    private String source; // GOOGLE or FALLBACK

    public TravelTimeDataDTO() {
    }

    public TravelTimeDataDTO(Double distanceKm, Integer travelTimeMinutes, Integer normalTravelTimeMinutes,
                             Integer trafficDelayMinutes, String trafficStatus, String source) {
        this.distanceKm = distanceKm;
        this.travelTimeMinutes = travelTimeMinutes;
        this.normalTravelTimeMinutes = normalTravelTimeMinutes;
        this.trafficDelayMinutes = trafficDelayMinutes;
        this.trafficStatus = trafficStatus;
        this.source = source;
    }

    public Double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(Double distanceKm) { this.distanceKm = distanceKm; }

    public Integer getTravelTimeMinutes() { return travelTimeMinutes; }
    public void setTravelTimeMinutes(Integer travelTimeMinutes) { this.travelTimeMinutes = travelTimeMinutes; }

    public Integer getNormalTravelTimeMinutes() { return normalTravelTimeMinutes; }
    public void setNormalTravelTimeMinutes(Integer normalTravelTimeMinutes) { this.normalTravelTimeMinutes = normalTravelTimeMinutes; }

    public Integer getTrafficDelayMinutes() { return trafficDelayMinutes; }
    public void setTrafficDelayMinutes(Integer trafficDelayMinutes) { this.trafficDelayMinutes = trafficDelayMinutes; }

    public String getTrafficStatus() { return trafficStatus; }
    public void setTrafficStatus(String trafficStatus) { this.trafficStatus = trafficStatus; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public static TravelTimeDataDTOBuilder builder() {
        return new TravelTimeDataDTOBuilder();
    }

    public static class TravelTimeDataDTOBuilder {
        private Double distanceKm;
        private Integer travelTimeMinutes;
        private Integer normalTravelTimeMinutes;
        private Integer trafficDelayMinutes;
        private String trafficStatus;
        private String source;

        public TravelTimeDataDTOBuilder distanceKm(Double distanceKm) { this.distanceKm = distanceKm; return this; }
        public TravelTimeDataDTOBuilder travelTimeMinutes(Integer travelTimeMinutes) { this.travelTimeMinutes = travelTimeMinutes; return this; }
        public TravelTimeDataDTOBuilder normalTravelTimeMinutes(Integer normalTravelTimeMinutes) { this.normalTravelTimeMinutes = normalTravelTimeMinutes; return this; }
        public TravelTimeDataDTOBuilder trafficDelayMinutes(Integer trafficDelayMinutes) { this.trafficDelayMinutes = trafficDelayMinutes; return this; }
        public TravelTimeDataDTOBuilder trafficStatus(String trafficStatus) { this.trafficStatus = trafficStatus; return this; }
        public TravelTimeDataDTOBuilder source(String source) { this.source = source; return this; }

        public TravelTimeDataDTO build() {
            return new TravelTimeDataDTO(distanceKm, travelTimeMinutes, normalTravelTimeMinutes,
                    trafficDelayMinutes, trafficStatus, source);
        }
    }
}
