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

    private final com.smartprocurement.repository.CentreRepository centreRepository;

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
        List<com.smartprocurement.entity.ProcurementCentre> centres = centreRepository.findAll();
        List<AdminDTO.QueuePrediction> predictions = new ArrayList<>();

        for (com.smartprocurement.entity.ProcurementCentre c : centres) {
            int currentQ = (int) (c.getCurrentLoad() > 0 ? (c.getCurrentLoad() / 50) : 0);
            predictions.add(AdminDTO.QueuePrediction.builder()
                    .centreId(c.getCentreId())
                    .centreName(c.getName())
                    .currentQueue(currentQ)
                    .predictedQueue1h(Math.max(1, currentQ + 5))
                    .predictedQueue2h(Math.max(2, currentQ + 12))
                    .predictedWaitMinutes(Math.max(15, currentQ * 3))
                    .confidenceScorePercent(88)
                    .dataType("AI_PREDICTION")
                    .build());
        }
        return predictions;
    }

    public List<AdminDTO.PeakHourPrediction> getPeakHourPredictions() {
        List<com.smartprocurement.entity.Booking> bookings = bookingRepository.findAll();
        if (bookings.isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.asList(
                AdminDTO.PeakHourPrediction.builder()
                        .timeWindow("09:00 AM – 11:00 AM")
                        .expectedFarmers(Math.max(10, bookings.size()))
                        .confidenceScorePercent(86)
                        .dataType("AI_PREDICTION")
                        .build(),
                AdminDTO.PeakHourPrediction.builder()
                        .timeWindow("02:00 PM – 04:00 PM")
                        .expectedFarmers(Math.max(5, bookings.size() / 2))
                        .confidenceScorePercent(84)
                        .dataType("AI_PREDICTION")
                        .build()
        );
    }

    public List<AdminDTO.TomorrowLoadPrediction> getTomorrowLoadPredictions() {
        List<com.smartprocurement.entity.ProcurementCentre> centres = centreRepository.findAll();
        List<AdminDTO.TomorrowLoadPrediction> list = new ArrayList<>();

        for (com.smartprocurement.entity.ProcurementCentre c : centres) {
            double loadPct = (c.getTotalCapacity() > 0) ? (c.getCurrentLoad() / c.getTotalCapacity()) * 100 : 0.0;
            list.add(AdminDTO.TomorrowLoadPrediction.builder()
                    .centreId(c.getCentreId())
                    .centreName(c.getName())
                    .todayLoadPercent(loadPct)
                    .predictedTomorrowLoadPercent(Math.min(100.0, loadPct + 10.0))
                    .expectedFarmers(25)
                    .expectedWaitMinutes(20)
                    .recommendedAction("Standard capacity available.")
                    .confidenceScorePercent(89)
                    .build());
        }
        return list;
    }

    public List<AdminDTO.CounterRecommendation> getCounterRecommendations() {
        List<com.smartprocurement.entity.ProcurementCentre> centres = centreRepository.findAll();
        List<AdminDTO.CounterRecommendation> recs = new ArrayList<>();

        for (com.smartprocurement.entity.ProcurementCentre c : centres) {
            recs.add(AdminDTO.CounterRecommendation.builder()
                    .centreId(c.getCentreId())
                    .centreName(c.getName())
                    .currentCounters(3)
                    .recommendedCounters(3)
                    .predictedDemandFarmersPerHour(30)
                    .currentCapacityFarmersPerHour(40)
                    .reason("Counter capacity is aligned with expected demand.")
                    .build());
        }
        return recs;
    }

    public List<AdminDTO.AnomalyAlert> getAnomalyAlerts() {
        return new ArrayList<>();
    }
}
