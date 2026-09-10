package com.smartprocurement.service;

import com.smartprocurement.dto.AdminDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AdminDistrictAnalyticsTest {

    @Autowired
    private DistrictAnalyticsService districtAnalyticsService;

    @Autowired
    private AiPredictionService aiPredictionService;

    @Autowired
    private EmergencyManagementService emergencyManagementService;

    @Test
    public void testDistrictOverviewAggregation() {
        AdminDTO.DistrictOverview overview = districtAnalyticsService.getDistrictOverview("TODAY");
        assertNotNull(overview);
        assertTrue(overview.getTotalCentres() >= 1);
        assertTrue(overview.getFarmersToday() >= 0);
        assertTrue(overview.getTotalProcurementKg() >= 0);
        assertTrue(overview.getAvgWaitTimeMinutes() > 0);
    }

    @Test
    public void testCentreHealthClassification() {
        List<AdminDTO.CentreHealthInfo> healthList = districtAnalyticsService.getCentreHealthList();
        assertNotNull(healthList);
        assertFalse(healthList.isEmpty());
        for (AdminDTO.CentreHealthInfo health : healthList) {
            assertNotNull(health.getCentreName());
            assertNotNull(health.getStatus());
            assertTrue(health.getCurrentLoadPercent() >= 0.0);
        }
    }

    @Test
    public void testQueueAndProcurementAnalytics() {
        AdminDTO.QueueAnalyticsInfo queueAn = districtAnalyticsService.getQueueAnalytics();
        assertNotNull(queueAn);
        assertTrue(queueAn.getTotalQueueLength() >= 0);

        AdminDTO.ProcurementAnalyticsInfo procAn = districtAnalyticsService.getProcurementAnalytics();
        assertNotNull(procAn);
        assertTrue(procAn.getTotalQuantityKg() >= 0);
    }

    @Test
    public void testAiInsightsPredictionsAndAnomalies() {
        AdminDTO.AiInsightsInfo insights = aiPredictionService.getAiInsights();
        assertNotNull(insights);

        assertFalse(insights.getQueuePredictions().isEmpty());
        AdminDTO.QueuePrediction qp = insights.getQueuePredictions().get(0);
        assertTrue(qp.getConfidenceScorePercent() > 0);
        assertNotNull(qp.getDataType());

        assertFalse(insights.getCounterRecommendations().isEmpty());
        AdminDTO.CounterRecommendation cr = insights.getCounterRecommendations().get(0);
        assertNotNull(cr.getReason());

        assertFalse(insights.getAnomalyAlerts().isEmpty());
        AdminDTO.AnomalyAlert anomaly = insights.getAnomalyAlerts().get(0);
        assertNotNull(anomaly.getAnomalyType());
    }

    @Test
    public void testEmergencyManagementAndAlternativeRanking() {
        List<AdminDTO.AlternativeCentreRank> alternatives = emergencyManagementService.triggerEmergencyClosure(
                1L, "EQUIPMENT_FAILURE");

        assertNotNull(alternatives);
        assertFalse(alternatives.isEmpty());

        AdminDTO.AlternativeCentreRank alt1 = alternatives.get(0);
        assertEquals(1, alt1.getRank());
        assertTrue(alt1.getDistanceKm() > 0);
        assertNotNull(alt1.getNotificationPayload());
        assertNotNull(alt1.getNotificationPayload().getTeluguMessage());
    }
}
