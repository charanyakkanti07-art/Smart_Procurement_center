package com.smartprocurement.service;

import com.smartprocurement.dto.VoiceEventPayloadDTO;
import com.smartprocurement.entity.*;
import com.smartprocurement.exception.BadRequestException;
import com.smartprocurement.exception.ResourceNotFoundException;
import com.smartprocurement.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class JourneyMonitoringService {

    private static final Logger logger = LoggerFactory.getLogger(JourneyMonitoringService.class);
    private static final int DEFAULT_TRAVEL_MINUTES = 45;
    private static final int DEFAULT_BUFFER_MINUTES = 15;
    private static final int MISSED_ARRIVAL_TOLERANCE_MINUTES = 15;
    private static final int MAX_CALL_RETRIES = 2;

    @Autowired
    private JourneyMonitoringRepository journeyRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ApprovalRequestRepository approvalRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private AuditService auditService;

    @Transactional
    public JourneyMonitoring scheduleJourney(Booking booking) {
        if (booking == null) return null;

        Optional<JourneyMonitoring> existing = journeyRepository.findByBookingBookingId(booking.getBookingId());
        if (existing.isPresent()) {
            return existing.get();
        }

        // Calculate Procurement Time (e.g. 11:00 AM on booking date)
        LocalDateTime procurementTime = resolveProcurementTime(booking.getBookingDate(), booking.getSlot());
        int travelMins = DEFAULT_TRAVEL_MINUTES;
        int bufferMins = DEFAULT_BUFFER_MINUTES;

        // AUTOMATIC CALL TIME = Procurement Time - Travel Time - Buffer Time
        LocalDateTime automaticCallTime = procurementTime.minusMinutes(travelMins + bufferMins);
        LocalDateTime expectedArrivalTime = automaticCallTime.plusMinutes(travelMins);

        JourneyMonitoring journey = JourneyMonitoring.builder()
                .booking(booking)
                .farmer(booking.getFarmer())
                .centre(booking.getCentre())
                .procurementTime(procurementTime)
                .estimatedTravelMinutes(travelMins)
                .bufferMinutes(bufferMins)
                .automaticCallTime(automaticCallTime)
                .expectedArrivalTime(expectedArrivalTime)
                .journeyStatus(JourneyStatus.SCHEDULED)
                .failedCallCount(0)
                .build();

        JourneyMonitoring saved = journeyRepository.save(journey);

        auditService.recordAudit("CALL_SCHEDULED", booking.getBookingId(), booking.getFarmer().getFarmerId(),
                booking.getCentre().getCentreId(), ApprovalActor.SYSTEM,
                String.format("Journey call scheduled for %s (Procurement: %s, Travel: %d min, Buffer: %d min)",
                        automaticCallTime, procurementTime, travelMins, bufferMins));

        return saved;
    }

    @Transactional
    public JourneyMonitoring processVoiceEvent(VoiceEventPayloadDTO payload) {
        if (payload == null || payload.getBookingId() == null) {
            throw new BadRequestException("Invalid voice event payload");
        }

        Booking booking = bookingRepository.findById(payload.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + payload.getBookingId()));

        JourneyMonitoring journey = journeyRepository.findByBookingBookingId(booking.getBookingId())
                .orElseGet(() -> scheduleJourney(booking));

        VoiceEventType event = payload.getEvent();
        ApprovalActor actor = "SIMULATOR".equalsIgnoreCase(payload.getSource()) ? ApprovalActor.SYSTEM : ApprovalActor.AI_AGENT;

        logger.info("Processing Voice Event: {} for Booking #{}, Source: {}", event, booking.getBookingId(), payload.getSource());

        // Audit call start/completed if passed
        if (event == VoiceEventType.CALL_STARTED) {
            journey.setLastCallTime(LocalDateTime.now());
            journey.setJourneyStatus(JourneyStatus.CALLED);
            auditService.recordAudit("CALL_STARTED", booking.getBookingId(), booking.getFarmer().getFarmerId(),
                    booking.getCentre().getCentreId(), actor, "AI Voice Call initiated");
            return journeyRepository.save(journey);
        }

        switch (event) {
            case FARMER_READY_TO_TRAVEL:
                journey.setJourneyStatus(JourneyStatus.TRAVELLING);
                LocalDateTime expectedArrival = LocalDateTime.now().plusMinutes(journey.getEstimatedTravelMinutes());
                journey.setExpectedArrivalTime(expectedArrival);
                // CRITICAL QUEUE SAFETY: Do NOT modify queue
                auditService.recordAudit("FARMER_READY_TO_TRAVEL", booking.getBookingId(), booking.getFarmer().getFarmerId(),
                        booking.getCentre().getCentreId(), actor, "Farmer confirmed ready to travel. Expected arrival: " + expectedArrival);
                break;

            case CANCELLATION_REQUESTED:
                journey.setJourneyStatus(JourneyStatus.CANCEL_REQUESTED);
                // Create PENDING ApprovalRequest. CRITICAL QUEUE SAFETY: Do NOT cancel booking, do NOT modify queue, do NOT release slot!
                createOrGetApprovalRequest(booking, ApprovalType.CANCELLATION, payload.getReason(), null, null, event, actor);
                auditService.recordAudit("CANCELLATION_REQUESTED", booking.getBookingId(), booking.getFarmer().getFarmerId(),
                        booking.getCentre().getCentreId(), actor, "AI Agent requested cancellation. PENDING owner approval. Reason: " + payload.getReason());
                notifyOwnerPendingApproval(booking, "Cancellation Request");
                break;

            case RESCHEDULE_REQUESTED:
                journey.setJourneyStatus(JourneyStatus.RESCHEDULE_REQUESTED);
                LocalDate reqDate = payload.getRequestedDate() != null ? payload.getRequestedDate() : LocalDate.now().plusDays(1);
                String reqSlot = payload.getRequestedTime() != null ? payload.getRequestedTime() : "Morning Slot";
                // Create PENDING ApprovalRequest. CRITICAL QUEUE SAFETY: Do NOT modify booking until owner approval.
                createOrGetApprovalRequest(booking, ApprovalType.RESCHEDULE, payload.getReason(), reqDate, reqSlot, event, actor);
                auditService.recordAudit("RESCHEDULE_REQUESTED", booking.getBookingId(), booking.getFarmer().getFarmerId(),
                        booking.getCentre().getCentreId(), actor, "AI Agent requested reschedule to " + reqDate + " (" + reqSlot + "). PENDING owner approval.");
                notifyOwnerPendingApproval(booking, "Reschedule Request");
                break;

            case NO_ANSWER:
            case FARMER_NOT_REACHABLE:
                int failed = journey.getFailedCallCount() + 1;
                journey.setFailedCallCount(failed);
                journey.setJourneyStatus(JourneyStatus.UNREACHABLE);
                journey.setLastCallTime(LocalDateTime.now());
                // CRITICAL QUEUE SAFETY: Do NOT cancel booking or remove from queue.
                auditService.recordAudit(event.name(), booking.getBookingId(), booking.getFarmer().getFarmerId(),
                        booking.getCentre().getCentreId(), actor, "Farmer call failed (Attempt " + failed + "/" + MAX_CALL_RETRIES + ")");
                if (failed >= MAX_CALL_RETRIES) {
                    notifyOwnerActionRequired(booking, "Unreachable Farmer");
                }
                break;

            case MISSED_EXPECTED_ARRIVAL:
                journey.setJourneyStatus(JourneyStatus.MISSED_ARRIVAL);
                // CRITICAL SAFETY: Never automatically cancel or reschedule or remove from queue!
                auditService.recordAudit("MISSED_EXPECTED_ARRIVAL", booking.getBookingId(), booking.getFarmer().getFarmerId(),
                        booking.getCentre().getCentreId(), actor, "Farmer missed expected arrival time. Action Required alert sent to owner.");
                notifyOwnerActionRequired(booking, "Missed Expected Arrival");
                break;

            default:
                break;
        }

        return journeyRepository.save(journey);
    }

    @Transactional
    public void checkForMissedArrivals(Long centreId) {
        List<JourneyMonitoring> travelling = centreId != null ?
                journeyRepository.findByCentreCentreIdAndJourneyStatus(centreId, JourneyStatus.TRAVELLING) :
                journeyRepository.findByJourneyStatus(JourneyStatus.TRAVELLING);

        LocalDateTime now = LocalDateTime.now();
        for (JourneyMonitoring j : travelling) {
            if (j.getBooking().getStatus() == BookingStatus.ARRIVED || j.getBooking().getStatus() == BookingStatus.PROCESSING || j.getBooking().getStatus() == BookingStatus.COMPLETED) {
                j.setJourneyStatus(JourneyStatus.ARRIVED);
                journeyRepository.save(j);
                continue;
            }
            if (j.getExpectedArrivalTime() != null && now.isAfter(j.getExpectedArrivalTime().plusMinutes(MISSED_ARRIVAL_TOLERANCE_MINUTES))) {
                VoiceEventPayloadDTO payload = VoiceEventPayloadDTO.builder()
                        .bookingId(j.getBooking().getBookingId())
                        .farmerId(j.getFarmer().getFarmerId())
                        .event(VoiceEventType.MISSED_EXPECTED_ARRIVAL)
                        .reason("Farmer did not check in within " + MISSED_ARRIVAL_TOLERANCE_MINUTES + " minutes of expected arrival")
                        .source("SYSTEM")
                        .build();
                processVoiceEvent(payload);
            }
        }
    }

    private void createOrGetApprovalRequest(Booking booking, ApprovalType type, String reason, LocalDate reqDate, String reqSlot, VoiceEventType event, ApprovalActor actor) {
        Optional<ApprovalRequest> existing = approvalRepository.findByBookingBookingIdAndTypeAndStatus(booking.getBookingId(), type, ApprovalStatus.PENDING);
        if (existing.isEmpty()) {
            ApprovalRequest req = ApprovalRequest.builder()
                    .booking(booking)
                    .farmer(booking.getFarmer())
                    .centre(booking.getCentre())
                    .type(type)
                    .status(ApprovalStatus.PENDING)
                    .createdBy(actor)
                    .reason(reason != null ? reason : "Requested by AI Agent")
                    .requestedDate(reqDate)
                    .requestedSlot(reqSlot)
                    .aiEvent(event)
                    .build();
            approvalRepository.save(req);
        }
    }

    private void notifyOwnerPendingApproval(Booking booking, String title) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("bookingId", booking.getBookingId());
            params.put("farmerName", booking.getFarmer().getName());
            notificationService.sendNotification(booking.getFarmer().getFarmerId(), NotificationEventType.RESCHEDULE_REQUEST, params, "pending_appr_booking_" + booking.getBookingId());
        } catch (Exception ignored) {
        }
    }

    private void notifyOwnerActionRequired(Booking booking, String title) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("bookingId", booking.getBookingId());
            params.put("farmerName", booking.getFarmer().getName());
            notificationService.sendNotification(booking.getFarmer().getFarmerId(), NotificationEventType.QUEUE_APPROACHING, params, "action_req_booking_" + booking.getBookingId());
        } catch (Exception ignored) {
        }
    }

    private LocalDateTime resolveProcurementTime(LocalDate bookingDate, String slot) {
        LocalDate date = bookingDate != null ? bookingDate : LocalDate.now();
        LocalTime time = LocalTime.of(11, 0); // Default 11 AM
        if (slot != null) {
            String s = slot.toLowerCase();
            if (s.contains("9:") || s.contains("9am")) {
                time = LocalTime.of(9, 30);
            } else if (s.contains("10:")) {
                time = LocalTime.of(10, 30);
            } else if (s.contains("11:")) {
                time = LocalTime.of(11, 0);
            } else if (s.contains("12:") || s.contains("afternoon")) {
                time = LocalTime.of(12, 30);
            } else if (s.contains("2:") || s.contains("3:") || s.contains("14:")) {
                time = LocalTime.of(14, 30);
            } else if (s.contains("morning")) {
                time = LocalTime.of(10, 0);
            }
        }
        return LocalDateTime.of(date, time);
    }
}
