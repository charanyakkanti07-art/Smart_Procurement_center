package com.smartprocurement.service;

import org.springframework.stereotype.Service;

@Service
public class TrafficService {

    public enum TrafficLevel {
        LOW,
        MODERATE,
        HIGH
    }

    public static class TrafficInfo {
        private final TrafficLevel level;
        private final double multiplier;
        private final String description;

        public TrafficInfo(TrafficLevel level, double multiplier, String description) {
            this.level = level;
            this.multiplier = multiplier;
            this.description = description;
        }

        public TrafficLevel getLevel() { return level; }
        public double getMultiplier() { return multiplier; }
        public String getDescription() { return description; }
    }

    /**
     * Determines traffic level for a given centre and distance.
     * In a production environment, this will delegate to Google Maps / OpenStreetMap API.
     * Labelled explicitly as "Simulated traffic engine".
     */
    public TrafficInfo getTrafficInfo(Long centreId, double distanceKm) {
        if (centreId != null && centreId == 1L) {
            // Centre A has HIGH traffic demo scenario
            return new TrafficInfo(TrafficLevel.HIGH, 1.8, "High traffic congestion (Simulated)");
        } else if (centreId != null && centreId == 2L) {
            // Centre B has LOW traffic demo scenario
            return new TrafficInfo(TrafficLevel.LOW, 1.0, "Low traffic / Clear highway (Simulated)");
        } else if (centreId != null && centreId == 3L) {
            // Centre C
            return new TrafficInfo(TrafficLevel.MODERATE, 1.25, "Moderate traffic (Simulated)");
        } else if (centreId != null && centreId == 4L) {
            // Centre D
            return new TrafficInfo(TrafficLevel.MODERATE, 1.3, "Moderate traffic (Simulated)");
        }

        // Generic fallback based on distance
        if (distanceKm < 8.0) {
            return new TrafficInfo(TrafficLevel.HIGH, 1.6, "City center traffic (Simulated)");
        } else if (distanceKm < 15.0) {
            return new TrafficInfo(TrafficLevel.MODERATE, 1.25, "Moderate suburban traffic (Simulated)");
        } else {
            return new TrafficInfo(TrafficLevel.LOW, 1.0, "Low highway traffic (Simulated)");
        }
    }

    public int calculateTravelTime(double distanceKm, TrafficInfo traffic) {
        double baseSpeedKmh = 30.0; // Base city/rural speed
        double travelHours = (distanceKm / baseSpeedKmh) * traffic.getMultiplier();
        return (int) Math.max(5, Math.round(travelHours * 60));
    }
}
