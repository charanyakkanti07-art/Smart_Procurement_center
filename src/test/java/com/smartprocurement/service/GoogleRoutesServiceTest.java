package com.smartprocurement.service;

import com.smartprocurement.dto.CentreRecommendationResponseDTO;
import com.smartprocurement.dto.GoogleRouteResult;
import com.smartprocurement.dto.RecommendationDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class GoogleRoutesServiceTest {

    @Autowired
    private GoogleRoutesService googleRoutesService;

    @Autowired
    private CentreRecommendationService recommendationService;

    @BeforeEach
    void setUp() {
        googleRoutesService.clearCache();
    }

    @Test
    void testFallbackResult() {
        // When Google API key is missing or fallback requested
        GoogleRouteResult result = googleRoutesService.getRoute(17.3850, 78.4867, 17.4350, 78.5800, 2L);

        assertNotNull(result);
        assertEquals("fallback", result.getTravelTimeSource());
        assertEquals("google", result.getProvider());
        assertEquals(15.0, result.getDistanceKm());
        assertEquals(30, result.getEffectiveTravelTimeMinutes());
        assertTrue(result.isTrafficAvailable());
    }

    @Test
    void testRouteCaching() {
        GoogleRouteResult first = googleRoutesService.getRoute(17.3850, 78.4867, 17.4350, 78.5800, 2L);
        GoogleRouteResult second = googleRoutesService.getRoute(17.3850, 78.4867, 17.4350, 78.5800, 2L);

        assertSame(first, second, "Subsequent route request within cache TTL must return exact cached reference");
    }

    @Test
    void testCentreBVsCentreARecommendationWithRoutes() {
        CentreRecommendationResponseDTO response = recommendationService.getRecommendations(17.3850, 78.4867, "Paddy", 500.0);

        assertNotNull(response);
        RecommendationDTO recommended = response.getRecommendedCentre();

        // Centre A: Google travel time (25 mins) + Queue wait (180 mins) = 205 mins total
        // Centre B: Google travel time (30 mins) + Queue wait (40 mins) = 70 mins total
        // Must recommend Centre B
        assertEquals(2L, recommended.getCentreId(), "Engine must recommend Centre B (70 min total) over Centre A (205 min total)");
        assertEquals(70, recommended.getEstimatedTotalMinutes());
        assertEquals(30, recommended.getTravelTimeMinutes());
        assertEquals(40, recommended.getEstimatedWaitMinutes());

        RecommendationDTO centreA = response.getCentres().stream()
                .filter(c -> c.getCentreId().equals(1L))
                .findFirst()
                .orElseThrow();

        assertEquals(205, centreA.getEstimatedTotalMinutes());
        assertEquals(25, centreA.getTravelTimeMinutes());
        assertEquals(180, centreA.getEstimatedWaitMinutes());
    }
}
