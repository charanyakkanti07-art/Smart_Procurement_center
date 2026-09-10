package com.smartprocurement.dto;

public class GoogleRouteResult {
    private Double distanceMeters;
    private Double distanceKm;
    private Integer durationSeconds;
    private Integer durationMinutes;
    private Integer trafficDurationSeconds;
    private Integer trafficDurationMinutes;
    private boolean trafficAvailable;
    private String travelTimeSource; // "google" or "fallback"
    private String provider; // "google"

    public GoogleRouteResult() {
    }

    public GoogleRouteResult(Double distanceMeters, Double distanceKm, Integer durationSeconds, Integer durationMinutes,
                             Integer trafficDurationSeconds, Integer trafficDurationMinutes, boolean trafficAvailable,
                             String travelTimeSource, String provider) {
        this.distanceMeters = distanceMeters;
        this.distanceKm = distanceKm;
        this.durationSeconds = durationSeconds;
        this.durationMinutes = durationMinutes;
        this.trafficDurationSeconds = trafficDurationSeconds;
        this.trafficDurationMinutes = trafficDurationMinutes;
        this.trafficAvailable = trafficAvailable;
        this.travelTimeSource = travelTimeSource;
        this.provider = provider;
    }

    public Double getDistanceMeters() { return distanceMeters; }
    public void setDistanceMeters(Double distanceMeters) { this.distanceMeters = distanceMeters; }

    public Double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(Double distanceKm) { this.distanceKm = distanceKm; }

    public Integer getDurationSeconds() { return durationSeconds; }
    public void setDurationSeconds(Integer durationSeconds) { this.durationSeconds = durationSeconds; }

    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }

    public Integer getTrafficDurationSeconds() { return trafficDurationSeconds; }
    public void setTrafficDurationSeconds(Integer trafficDurationSeconds) { this.trafficDurationSeconds = trafficDurationSeconds; }

    public Integer getTrafficDurationMinutes() { return trafficDurationMinutes; }
    public void setTrafficDurationMinutes(Integer trafficDurationMinutes) { this.trafficDurationMinutes = trafficDurationMinutes; }

    public boolean isTrafficAvailable() { return trafficAvailable; }
    public void setTrafficAvailable(boolean trafficAvailable) { this.trafficAvailable = trafficAvailable; }

    public String getTravelTimeSource() { return travelTimeSource; }
    public void setTravelTimeSource(String travelTimeSource) { this.travelTimeSource = travelTimeSource; }

    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }

    public int getEffectiveTravelTimeMinutes() {
        if (trafficAvailable && trafficDurationMinutes != null && trafficDurationMinutes > 0) {
            return trafficDurationMinutes;
        }
        return (durationMinutes != null && durationMinutes > 0) ? durationMinutes : 15;
    }

    public static GoogleRouteResultBuilder builder() {
        return new GoogleRouteResultBuilder();
    }

    public static class GoogleRouteResultBuilder {
        private Double distanceMeters;
        private Double distanceKm;
        private Integer durationSeconds;
        private Integer durationMinutes;
        private Integer trafficDurationSeconds;
        private Integer trafficDurationMinutes;
        private boolean trafficAvailable;
        private String travelTimeSource;
        private String provider;

        public GoogleRouteResultBuilder distanceMeters(Double distanceMeters) { this.distanceMeters = distanceMeters; return this; }
        public GoogleRouteResultBuilder distanceKm(Double distanceKm) { this.distanceKm = distanceKm; return this; }
        public GoogleRouteResultBuilder durationSeconds(Integer durationSeconds) { this.durationSeconds = durationSeconds; return this; }
        public GoogleRouteResultBuilder durationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; return this; }
        public GoogleRouteResultBuilder trafficDurationSeconds(Integer trafficDurationSeconds) { this.trafficDurationSeconds = trafficDurationSeconds; return this; }
        public GoogleRouteResultBuilder trafficDurationMinutes(Integer trafficDurationMinutes) { this.trafficDurationMinutes = trafficDurationMinutes; return this; }
        public GoogleRouteResultBuilder trafficAvailable(boolean trafficAvailable) { this.trafficAvailable = trafficAvailable; return this; }
        public GoogleRouteResultBuilder travelTimeSource(String travelTimeSource) { this.travelTimeSource = travelTimeSource; return this; }
        public GoogleRouteResultBuilder provider(String provider) { this.provider = provider; return this; }

        public GoogleRouteResult build() {
            return new GoogleRouteResult(distanceMeters, distanceKm, durationSeconds, durationMinutes,
                    trafficDurationSeconds, trafficDurationMinutes, trafficAvailable, travelTimeSource, provider);
        }
    }
}
