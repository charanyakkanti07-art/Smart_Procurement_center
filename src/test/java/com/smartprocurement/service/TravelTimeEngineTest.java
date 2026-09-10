package com.smartprocurement.service;

import com.smartprocurement.dto.GoogleRouteResult;
import com.smartprocurement.dto.LocationDTO;
import com.smartprocurement.dto.TravelTimeResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

public class TravelTimeEngineTest {

    private TravelTimeEngine travelTimeEngine;
    private StubGoogleRoutesService stubGoogleRoutesService;

    private static class StubGoogleRoutesService extends GoogleRoutesService {
        GoogleRouteResult resultToReturn;
        boolean shouldThrow = false;

        @Override
        public GoogleRouteResult getRoute(double originLat, double originLon, double destLat, double destLon, Long centreId) {
            if (shouldThrow) {
                throw new RuntimeException("Google API Error");
            }
            if (resultToReturn != null) {
                return resultToReturn;
            }
            return buildFallbackResult(originLat, originLon, destLat, destLon, centreId);
        }
    }

    @BeforeEach
    void setUp() {
        stubGoogleRoutesService = new StubGoogleRoutesService();
        travelTimeEngine = new TravelTimeEngine();
        ReflectionTestUtils.setField(travelTimeEngine, "googleRoutesService", stubGoogleRoutesService);
    }

    @Test
    void testValidCoordinates() {
        LocationDTO origin = new LocationDTO(17.3850, 78.4867);
        LocationDTO destination = new LocationDTO(17.4200, 78.4500);

        stubGoogleRoutesService.resultToReturn = GoogleRouteResult.builder()
                .distanceMeters(8200.0)
                .distanceKm(8.2)
                .durationSeconds(1320)
                .durationMinutes(22)
                .trafficDurationSeconds(1860)
                .trafficDurationMinutes(31)
                .trafficAvailable(true)
                .travelTimeSource("google")
                .provider("google")
                .build();

        TravelTimeResponseDTO response = travelTimeEngine.calculateTravelTime(origin, destination);

        assertTrue(response.isSuccess());
        assertNotNull(response.getData());
        assertEquals(8.2, response.getData().getDistanceKm());
        assertEquals(31, response.getData().getTravelTimeMinutes());
        assertEquals(22, response.getData().getNormalTravelTimeMinutes());
        assertEquals(9, response.getData().getTrafficDelayMinutes());
        assertEquals("MODERATE", response.getData().getTrafficStatus());
        assertEquals("GOOGLE", response.getData().getSource());
    }

    @Test
    void testInvalidOriginLatitude() {
        LocationDTO origin = new LocationDTO(-95.0, 78.4867);
        LocationDTO destination = new LocationDTO(17.4200, 78.4500);

        TravelTimeResponseDTO response = travelTimeEngine.calculateTravelTime(origin, destination);

        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("latitude"));
    }

    @Test
    void testInvalidOriginLongitude() {
        LocationDTO origin = new LocationDTO(17.3850, 185.0);
        LocationDTO destination = new LocationDTO(17.4200, 78.4500);

        TravelTimeResponseDTO response = travelTimeEngine.calculateTravelTime(origin, destination);

        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("longitude"));
    }

    @Test
    void testMissingOrigin() {
        LocationDTO destination = new LocationDTO(17.4200, 78.4500);

        TravelTimeResponseDTO response = travelTimeEngine.calculateTravelTime(null, destination);

        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("Origin"));
    }

    @Test
    void testMissingDestination() {
        LocationDTO origin = new LocationDTO(17.3850, 78.4867);

        TravelTimeResponseDTO response = travelTimeEngine.calculateTravelTime(origin, null);

        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("Destination"));
    }

    @Test
    void testSuccessfulMockRouteResponse() {
        // Prompt scenario: distance = 8200m (8.2km), normal = 22m, traffic = 31m
        LocationDTO origin = new LocationDTO(17.3850, 78.4867);
        LocationDTO destination = new LocationDTO(17.4200, 78.4500);

        stubGoogleRoutesService.resultToReturn = GoogleRouteResult.builder()
                .distanceMeters(8200.0)
                .distanceKm(8.2)
                .durationSeconds(1320)
                .durationMinutes(22)
                .trafficDurationSeconds(1860)
                .trafficDurationMinutes(31)
                .trafficAvailable(true)
                .travelTimeSource("google")
                .provider("google")
                .build();

        TravelTimeResponseDTO response = travelTimeEngine.calculateTravelTime(origin, destination);

        assertTrue(response.isSuccess());
        assertEquals(8.2, response.getData().getDistanceKm());
        assertEquals(22, response.getData().getNormalTravelTimeMinutes());
        assertEquals(31, response.getData().getTravelTimeMinutes());
        assertEquals(9, response.getData().getTrafficDelayMinutes());
        assertEquals("MODERATE", response.getData().getTrafficStatus());
        assertEquals("GOOGLE", response.getData().getSource());
    }

    @Test
    void testTrafficStatusClassification() {
        assertEquals("LOW", travelTimeEngine.classifyTrafficStatus(3, true));
        assertEquals("MODERATE", travelTimeEngine.classifyTrafficStatus(12, true));
        assertEquals("HIGH", travelTimeEngine.classifyTrafficStatus(25, true));
        assertEquals("SEVERE", travelTimeEngine.classifyTrafficStatus(40, true));
    }

    @Test
    void testGoogleApiFailureFallback() {
        LocationDTO origin = new LocationDTO(17.3850, 78.4867);
        LocationDTO destination = new LocationDTO(17.4200, 78.4500);

        stubGoogleRoutesService.shouldThrow = true;

        TravelTimeResponseDTO response = travelTimeEngine.calculateTravelTime(origin, destination);

        assertTrue(response.isSuccess());
        assertNotNull(response.getData());
        assertEquals("FALLBACK", response.getData().getSource());
        assertEquals("UNAVAILABLE", response.getData().getTrafficStatus());
        assertNull(response.getData().getTravelTimeMinutes());
        assertNotNull(response.getData().getDistanceKm());
    }

    @Test
    void testMissingGoogleApiKey() {
        LocationDTO origin = new LocationDTO(17.3850, 78.4867);
        LocationDTO destination = new LocationDTO(17.4200, 78.4500);

        stubGoogleRoutesService.resultToReturn = GoogleRouteResult.builder()
                .distanceMeters(8200.0)
                .distanceKm(8.2)
                .durationSeconds(1320)
                .durationMinutes(22)
                .trafficDurationSeconds(1860)
                .trafficDurationMinutes(31)
                .trafficAvailable(false)
                .travelTimeSource("fallback")
                .provider("google")
                .build();

        TravelTimeResponseDTO response = travelTimeEngine.calculateTravelTime(origin, destination);

        assertTrue(response.isSuccess());
        assertEquals("FALLBACK", response.getData().getSource());
        assertEquals("UNAVAILABLE", response.getData().getTrafficStatus());
    }

    @Test
    void testNoRouteAvailable() {
        LocationDTO origin = new LocationDTO(17.3850, 78.4867);
        LocationDTO destination = new LocationDTO(17.4200, 78.4500);

        stubGoogleRoutesService.resultToReturn = GoogleRouteResult.builder()
                .distanceMeters(0.0)
                .distanceKm(0.0)
                .durationSeconds(0)
                .durationMinutes(0)
                .trafficDurationSeconds(0)
                .trafficDurationMinutes(0)
                .trafficAvailable(false)
                .travelTimeSource("fallback")
                .provider("google")
                .build();

        TravelTimeResponseDTO response = travelTimeEngine.calculateTravelTime(origin, destination);

        assertTrue(response.isSuccess());
        assertEquals("FALLBACK", response.getData().getSource());
    }
}
