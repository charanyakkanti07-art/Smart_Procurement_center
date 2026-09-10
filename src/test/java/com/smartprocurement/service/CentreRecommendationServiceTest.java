package com.smartprocurement.service;

import com.smartprocurement.dto.CentreRecommendationResponseDTO;
import com.smartprocurement.dto.RecommendationDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CentreRecommendationServiceTest {

    @Autowired
    private CentreRecommendationService recommendationService;

    @Test
    void testHaversineDistance() {
        // Distance between Kondapur (17.3850, 78.4867) and nearby point (17.3912, 78.4920)
        double distance = recommendationService.calculateHaversineDistance(17.3850, 78.4867, 17.3912, 78.4920);
        assertTrue(distance > 0.5 && distance < 2.0, "Haversine distance calculation should be approximately 0.9 km");
    }

    @Test
    void testIntelligentRecommendation_RecommendsCentreBOverCentreA() {
        // Run recommendation engine with default farmer location
        CentreRecommendationResponseDTO response = recommendationService.getRecommendations(17.3850, 78.4867, "Paddy", 500.0);

        assertNotNull(response);
        assertNotNull(response.getRecommendedCentre());

        RecommendationDTO recommended = response.getRecommendedCentre();

        // Must recommend Centre B (15 km away, 40 min wait) over Centre A (8 km away, 180 min wait)
        assertEquals(2L, recommended.getCentreId(), "Engine must recommend Centre B due to lower total waiting + travel time");
        assertEquals("RECOMMENDED", recommended.getRecommendationBadge());
        assertTrue(recommended.getEstimatedTotalMinutes() < 90, "Centre B total time should be ~70 mins");

        // Verify summary explicitly explains why farther centre was chosen
        assertTrue(response.getRecommendationSummary().contains("Centre B is recommended even though it is farther away"),
                "Recommendation summary must explain why Centre B was chosen");
    }

    @Test
    void testClosedCentreExclusion() {
        CentreRecommendationResponseDTO response = recommendationService.getRecommendations(17.3850, 78.4867, "Paddy", 500.0);

        RecommendationDTO centreC = response.getCentres().stream()
                .filter(c -> c.getCentreId().equals(3L))
                .findFirst()
                .orElseThrow();

        assertEquals("CLOSED", centreC.getOperatingStatus());
        assertEquals("CLOSED", centreC.getAvailability());
        assertTrue(centreC.getWarnings().contains("Centre is currently closed"));
        assertNotEquals(response.getRecommendedCentre().getCentreId(), centreC.getCentreId(), "Closed centre must not be recommended");
    }

    @Test
    void testOverloadedCentrePenalty() {
        CentreRecommendationResponseDTO response = recommendationService.getRecommendations(17.3850, 78.4867, "Paddy", 500.0);

        RecommendationDTO centreD = response.getCentres().stream()
                .filter(c -> c.getCentreId().equals(4L))
                .findFirst()
                .orElseThrow();

        assertEquals("OVERLOADED", centreD.getOperatingStatus());
        assertEquals("OVERLOADED", centreD.getAvailability());
        assertTrue(centreD.getLoadPercentage() > 100.0);
        assertTrue(centreD.getWarnings().stream().anyMatch(w -> w.contains("overloaded")));
    }

    @Test
    void testExplanationGeneration() {
        CentreRecommendationResponseDTO response = recommendationService.getRecommendations(17.3850, 78.4867, "Paddy", 500.0);

        RecommendationDTO centreA = response.getCentres().stream()
                .filter(c -> c.getCentreId().equals(1L))
                .findFirst()
                .orElseThrow();

        RecommendationDTO centreB = response.getCentres().stream()
                .filter(c -> c.getCentreId().equals(2L))
                .findFirst()
                .orElseThrow();

        // Centre A should have warnings about queue and wait time
        assertTrue(centreA.getWarnings().stream().anyMatch(w -> w.contains("High queue") || w.contains("wait")));

        // Centre B should have positive reasons
        assertTrue(centreB.getReasons().stream().anyMatch(r -> r.contains("Short queue") || r.contains("Low traffic")));
        assertTrue(centreB.getWarnings().stream().anyMatch(w -> w.contains("15")));
    }
}
