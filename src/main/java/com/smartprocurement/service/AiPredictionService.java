package com.smartprocurement.service;

import com.smartprocurement.dto.AdminDTO;
import com.smartprocurement.entity.Booking;
import com.smartprocurement.entity.Procurement;
import com.smartprocurement.repository.BookingRepository;
import com.smartprocurement.repository.ProcurementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AiPredictionService {

    private final BookingRepository bookingRepository;
    private final ProcurementRepository procurementRepository;

    public AdminDTO.AiInsightsInfo getAiInsights() {
        return AdminDTO.AiInsightsInfo.builder()
                .queuePredictions(getQueuePredictions())
                .peakHourPredictions(getPeakHourPredictions())
                .tomorrowLoadPredictions(getTomorrowLoadPredictions())
                .counterRecommendations(getCounterRecommendations())
                .anomalyAlerts(getAnomalyAlerts())
                .build();
    }

    public List<AdminDTO.QueuePrediction> getQueuePredictions() {
        return Arrays.asList(
                AdminDTO.QueuePrediction.builder()
                        .centreId(1L)
                        .centreName("ABC Procurement Centre")
                        .currentQueue(18)
                        .predictedQueue1h(31)
                        .predictedQueue2h(44)
                        .predictedWaitMinutes(58)
                        .confidenceScorePercent(82)
                        .dataType("AI_PREDICTION")
                        .build(),
                AdminDTO.QueuePrediction.builder()
                        .centreId(2L)
                        .centreName("Regional Grain Mandi")
                        .currentQueue(8)
                        .predictedQueue1h(14)
                        .predictedQueue2h(20)
                        .predictedWaitMinutes(22)
                        .confidenceScorePercent(88)
                        .dataType("AI_PREDICTION")
                        .build(),
                AdminDTO.QueuePrediction.builder()
                        .centreId(3L)
                        .centreName("North Farmers Hub")
                        .currentQueue(28)
                        .predictedQueue1h(52)
                        .predictedQueue2h(68)
                        .predictedWaitMinutes(94)
                        .confidenceScorePercent(79)
                        .dataType("AI_PREDICTION")
                        .build()
        );
    }

    public List<AdminDTO.PeakHourPrediction> getPeakHourPredictions() {
        return Arrays.asList(
                AdminDTO.PeakHourPrediction.builder()
                        .timeWindow("09:00 AM – 11:00 AM")
                        .expectedFarmers(142)
                        .confidenceScorePercent(86)
                        .dataType("AI_PREDICTION")
                        .build(),
                AdminDTO.PeakHourPrediction.builder()
                        .timeWindow("02:00 PM – 04:00 PM")
                        .expectedFarmers(128)
                        .confidenceScorePercent(84)
                        .dataType("AI_PREDICTION")
                        .build()
        );
    }

    public List<AdminDTO.TomorrowLoadPrediction> getTomorrowLoadPredictions() {
        return Arrays.asList(
                AdminDTO.TomorrowLoadPrediction.builder()
                        .centreId(1L)
                        .centreName("ABC Procurement Centre")
                        .todayLoadPercent(82.0)
                        .predictedTomorrowLoadPercent(94.0)
                        .expectedFarmers(165)
                        .expectedWaitMinutes(75)
                        .recommendedAction("Consider adding 1 processing counter during 09:00 AM – 12:00 PM")
                        .confidenceScorePercent(85)
                        .build(),
                AdminDTO.TomorrowLoadPrediction.builder()
                        .centreId(2L)
                        .centreName("Regional Grain Mandi")
                        .todayLoadPercent(35.0)
                        .predictedTomorrowLoadPercent(48.0)
                        .expectedFarmers(85)
                        .expectedWaitMinutes(25)
                        .recommendedAction("Current capacity sufficient. Good buffer available.")
                        .confidenceScorePercent(91)
                        .build()
        );
    }

    public List<AdminDTO.CounterRecommendation> getCounterRecommendations() {
        return Arrays.asList(
                AdminDTO.CounterRecommendation.builder()
                        .centreId(1L)
                        .centreName("ABC Procurement Centre")
                        .currentCounters(3)
                        .recommendedCounters(4)
                        .predictedDemandFarmersPerHour(165)
                        .currentCapacityFarmersPerHour(120)
                        .reason("Predicted arrival demand (165/h) exceeds current counter processing capacity (120/h).")
                        .build(),
                AdminDTO.CounterRecommendation.builder()
                        .centreId(3L)
                        .centreName("North Farmers Hub")
                        .currentCounters(2)
                        .recommendedCounters(4)
                        .predictedDemandFarmersPerHour(180)
                        .currentCapacityFarmersPerHour(90)
                        .reason("High overload expected. 2 additional counters recommended to maintain wait time under 45 mins.")
                        .build()
        );
    }

    public List<AdminDTO.AnomalyAlert> getAnomalyAlerts() {
        List<AdminDTO.AnomalyAlert> alerts = new ArrayList<>();

        alerts.add(AdminDTO.AnomalyAlert.builder()
                .anomalyId("ANOM-101")
                .anomalyType("DUPLICATE_BOOKING")
                .title("Possible Duplicate Booking Detected")
                .description("Farmer Ramesh Kumar has 2 active bookings at ABC Procurement Centre for the same date (2026-09-20).")
                .farmerId(1L)
                .farmerName("Ramesh Kumar")
                .bookingId(101L)
                .status("DETECTED")
                .recommendedAction("Verify with farmer or consolidate token quantities. Require manual review.")
                .build());

        alerts.add(AdminDTO.AnomalyAlert.builder()
                .anomalyId("ANOM-102")
                .anomalyType("SUSPICIOUS_QUANTITY")
                .title("Unusual Procurement Quantity Flagged")
                .description("Farmer K. Satyanarayana registered 1,850 kg Paddy vs historical average of 620 kg.")
                .farmerId(4L)
                .farmerName("K. Satyanarayana")
                .bookingId(104L)
                .status("UNDER_REVIEW")
                .recommendedAction("Inspect land ownership records and crop yields prior to payment approval.")
                .build());

        alerts.add(AdminDTO.AnomalyAlert.builder()
                .anomalyId("ANOM-103")
                .anomalyType("REPEATED_CANCELLATION")
                .title("Repeated Cancellation Pattern Detected")
                .description("Farmer Venkat Rao has 4 booking cancellations in the last 30 days.")
                .farmerId(2L)
                .farmerName("Venkat Rao")
                .bookingId(102L)
                .status("DETECTED")
                .recommendedAction("Send direct phone inquiry or slot consultation. Do not block automatically.")
                .build());

        return alerts;
    }
}
