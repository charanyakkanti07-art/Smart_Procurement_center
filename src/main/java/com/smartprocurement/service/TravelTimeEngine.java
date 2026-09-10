package com.smartprocurement.service;

import com.smartprocurement.dto.GoogleRouteResult;
import com.smartprocurement.dto.LocationDTO;
import com.smartprocurement.dto.TravelTimeDataDTO;
import com.smartprocurement.dto.TravelTimeResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TravelTimeEngine {

    private static final Logger logger = LoggerFactory.getLogger(TravelTimeEngine.class);

    @Autowired
    private GoogleRoutesService googleRoutesService;

    public TravelTimeResponseDTO calculateTravelTime(LocationDTO origin, LocationDTO destination) {
        // 1. Validation
        String validationError = validateCoordinates(origin, destination);
        if (validationError != null) {
            logger.warn("Travel-time request validation failed: {}", validationError);
            return TravelTimeResponseDTO.error(validationError);
        }

        double origLat = origin.getLatitude();
        double origLon = origin.getLongitude();
        double destLat = destination.getLatitude();
        double destLon = destination.getLongitude();

        // 2. Logging Request (Safe: No API keys or tokens logged)
        logger.info("Travel-time request received.");
        logger.info("Origin coordinates: lat={}, lon={}", origLat, origLon);
        logger.info("Destination coordinates: lat={}, lon={}", destLat, destLon);

        try {
            // 3. Delegate to GoogleRoutesService
            GoogleRouteResult routeResult = googleRoutesService.getRoute(origLat, origLon, destLat, destLon, null);

            boolean isGoogleSource = "google".equalsIgnoreCase(routeResult.getTravelTimeSource());
            String source = isGoogleSource ? "GOOGLE" : "FALLBACK";

            Double distanceKm = routeResult.getDistanceKm();
            Integer travelTimeMinutes = routeResult.getEffectiveTravelTimeMinutes();
            Integer normalTravelTimeMinutes = routeResult.getDurationMinutes();

            Integer trafficDelayMinutes = null;
            String trafficStatus;

            if (!isGoogleSource) {
                // Fallback Engine Mode
                trafficStatus = "UNAVAILABLE";
                travelTimeMinutes = null;
                normalTravelTimeMinutes = null;
            } else {
                if (travelTimeMinutes != null && normalTravelTimeMinutes != null) {
                    trafficDelayMinutes = Math.max(0, travelTimeMinutes - normalTravelTimeMinutes);
                    trafficStatus = classifyTrafficStatus(trafficDelayMinutes, routeResult.isTrafficAvailable());
                } else {
                    trafficStatus = "UNKNOWN";
                }
            }

            TravelTimeDataDTO data = TravelTimeDataDTO.builder()
                    .distanceKm(distanceKm)
                    .travelTimeMinutes(travelTimeMinutes)
                    .normalTravelTimeMinutes(normalTravelTimeMinutes)
                    .trafficDelayMinutes(trafficDelayMinutes)
                    .trafficStatus(trafficStatus)
                    .source(source)
                    .build();

            logger.info("Routing API success. Source={}, Distance={} km, TravelTime={} mins, TrafficStatus={}",
                    source, distanceKm, travelTimeMinutes, trafficStatus);

            return TravelTimeResponseDTO.success(data);

        } catch (Exception e) {
            logger.error("Error occurred during travel time computation: {}. Executing fallback response.", e.getMessage());

            // Clean fallback response without crashing
            double distanceKm = calculateHaversineDistance(origLat, origLon, destLat, destLon);

            TravelTimeDataDTO fallbackData = TravelTimeDataDTO.builder()
                    .distanceKm(distanceKm)
                    .travelTimeMinutes(null)
                    .normalTravelTimeMinutes(null)
                    .trafficDelayMinutes(null)
                    .trafficStatus("UNAVAILABLE")
                    .source("FALLBACK")
                    .build();

            return TravelTimeResponseDTO.success(fallbackData);
        }
    }

    public String validateCoordinates(LocationDTO origin, LocationDTO destination) {
        if (origin == null) {
            return "Origin location is required.";
        }
        if (destination == null) {
            return "Destination location is required.";
        }
        if (origin.getLatitude() == null || origin.getLatitude() < -90.0 || origin.getLatitude() > 90.0) {
            return "Origin latitude must be between -90 and 90.";
        }
        if (origin.getLongitude() == null || origin.getLongitude() < -180.0 || origin.getLongitude() > 180.0) {
            return "Origin longitude must be between -180 and 180.";
        }
        if (destination.getLatitude() == null || destination.getLatitude() < -90.0 || destination.getLatitude() > 90.0) {
            return "Destination latitude must be between -90 and 90.";
        }
        if (destination.getLongitude() == null || destination.getLongitude() < -180.0 || destination.getLongitude() > 180.0) {
            return "Destination longitude must be between -180 and 180.";
        }
        return null;
    }

    public String classifyTrafficStatus(int delayMinutes, boolean trafficAvailable) {
        if (!trafficAvailable && delayMinutes == 0) {
            return "LOW";
        }
        if (delayMinutes <= 5) {
            return "LOW";
        } else if (delayMinutes <= 15) {
            return "MODERATE";
        } else if (delayMinutes <= 30) {
            return "HIGH";
        } else {
            return "SEVERE";
        }
    }

    public double calculateHaversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final int EARTH_RADIUS_KM = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return Math.round(EARTH_RADIUS_KM * c * 10.0) / 10.0;
    }
}
