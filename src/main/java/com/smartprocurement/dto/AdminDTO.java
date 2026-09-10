package com.smartprocurement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class AdminDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DistrictOverview {
        private int totalCentres;
        private int activeCentres;
        private int farmersToday;
        private double totalProcurementKg;
        private int avgWaitTimeMinutes;
        private int avgProcessingTimeMinutes;
        private int cancellationsCount;
        private int reschedulingCount;
        private int noShowsCount;
        private double totalPaymentPendingRs;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CentreHealthInfo {
        private Long centreId;
        private String centreName;
        private String location;
        private String status;
        private double currentLoadPercent;
        private double capacityKg;
        private double currentLoadKg;
        private int waitingFarmersCount;
        private int processingFarmersCount;
        private int activeCounters;
        private int estimatedWaitMinutes;
        private String overloadReason;
        private int todaysFarmersCount;
        private double todaysProcurementKg;
        private int cancellationsCount;
        private int noShowsCount;
        private double paymentPendingRs;
        private double avgWaitTimeMinutes;
        private double avgProcessingTimeMinutes;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class QueueAnalyticsInfo {
        private int totalQueueLength;
        private int avgQueueLength;
        private int avgWaitMinutes;
        private int maxWaitMinutes;
        private int farmersServedToday;
        private int farmersWaiting;
        private int farmersProcessing;
        private int queueCancellations;
        private int queueNoShows;
        private List<HourlyTrendPoint> hourlyTrends;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class HourlyTrendPoint {
        private String hour;
        private int waitingCount;
        private int processedCount;
        private int avgWaitMinutes;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProcurementAnalyticsInfo {
        private double totalQuantityKg;
        private int totalTransactionsCount;
        private double avgQuantityPerFarmerKg;
        private int completedProcurementsCount;
        private int failedProcurementsCount;
        private List<CropVolume> cropVolumes;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CropVolume {
        private String cropType;
        private double quantityKg;
        private double totalAmountRs;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AiInsightsInfo {
        private List<QueuePrediction> queuePredictions;
        private List<PeakHourPrediction> peakHourPredictions;
        private List<TomorrowLoadPrediction> tomorrowLoadPredictions;
        private List<CounterRecommendation> counterRecommendations;
        private List<AnomalyAlert> anomalyAlerts;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class QueuePrediction {
        private Long centreId;
        private String centreName;
        private int currentQueue;
        private int predictedQueue1h;
        private int predictedQueue2h;
        private int predictedWaitMinutes;
        private int confidenceScorePercent;
        private String dataType;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PeakHourPrediction {
        private String timeWindow;
        private int expectedFarmers;
        private int confidenceScorePercent;
        private String dataType;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TomorrowLoadPrediction {
        private Long centreId;
        private String centreName;
        private double todayLoadPercent;
        private double predictedTomorrowLoadPercent;
        private int expectedFarmers;
        private int expectedWaitMinutes;
        private String recommendedAction;
        private int confidenceScorePercent;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CounterRecommendation {
        private Long centreId;
        private String centreName;
        private int currentCounters;
        private int recommendedCounters;
        private int predictedDemandFarmersPerHour;
        private int currentCapacityFarmersPerHour;
        private String reason;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AnomalyAlert {
        private String anomalyId;
        private String anomalyType;
        private String title;
        private String description;
        private Long farmerId;
        private String farmerName;
        private Long bookingId;
        private String status;
        private String recommendedAction;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EmergencyClosureRequest {
        private Long centreId;
        private String reason;
        private String closureNotes;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AlternativeCentreRank {
        private Long centreId;
        private String centreName;
        private double distanceKm;
        private int travelTimeMinutes;
        private int expectedWaitMinutes;
        private double currentLoadPercent;
        private String status;
        private int rank;
        private MultilingualNotificationPayload notificationPayload;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MultilingualNotificationPayload {
        private String englishMessage;
        private String teluguMessage;
        private String hindiMessage;
        private String affectedFarmerPhone;
        private Long bookingId;
    }
}
