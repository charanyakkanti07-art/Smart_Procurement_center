package com.smartprocurement.service;

import com.smartprocurement.dto.ActionRequiredDTO;
import com.smartprocurement.dto.ApprovalRequestDTO;
import com.smartprocurement.dto.VoiceEventPayloadDTO;
import com.smartprocurement.entity.*;
import com.smartprocurement.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class JourneyMonitoringTest {

    @Autowired
    private JourneyMonitoringService journeyService;

    @Autowired
    private ApprovalService approvalService;

    @Autowired
    private QueueService queueService;

    @Autowired
    private JourneyMonitoringRepository journeyRepository;

    @Autowired
    private ApprovalRequestRepository approvalRepository;

    @Autowired
    private AuditLogRepository auditRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private FarmerRepository farmerRepository;

    @Autowired
    private CentreRepository centreRepository;

    @Autowired
    private CropRepository cropRepository;

    @Autowired
    private UserRepository userRepository;

    private ProcurementCentre centre;
    private Farmer farmer1;
    private Crop crop;
    private Booking booking1;
    private User owner;

    @BeforeEach
    @Transactional
    public void setup() {
        auditRepository.deleteAll();
        approvalRepository.deleteAll();
        journeyRepository.deleteAll();
        bookingRepository.deleteAll();
        cropRepository.deleteAll();
        farmerRepository.deleteAll();
        userRepository.deleteAll();
        centreRepository.deleteAll();

        centre = centreRepository.save(ProcurementCentre.builder()
                .name("Centre A")
                .location("Hyderabad")
                .totalCapacity(1000.0)
                .currentLoad(100.0)
                .status(CentreStatus.ACTIVE)
                .build());

        owner = userRepository.save(User.builder()
                .phone("9876543210")
                .name("Owner Ramu")
                .password("password123")
                .role(Role.OWNER)
                .centre(centre)
                .build());

        farmer1 = farmerRepository.save(Farmer.builder()
                .name("Farmer Ramesh")
                .phone("9000000001")
                .language("English")
                .build());

        crop = cropRepository.save(Crop.builder()
                .cropType("Paddy")
                .quantity(500.0)
                .farmer(farmer1)
                .build());

        booking1 = bookingRepository.save(Booking.builder()
                .farmer(farmer1)
                .centre(centre)
                .crop(crop)
                .quantity(100.0)
                .bookingDate(LocalDate.now())
                .slot("11:00 AM – 12:00 PM")
                .status(BookingStatus.WAITING)
                .build());
    }

    @Test
    @Transactional
    public void testScenario1AutomaticCallTimeCalculationAndFarmerReady() {
        // Schedule journey
        JourneyMonitoring journey = journeyService.scheduleJourney(booking1);

        assertNotNull(journey);
        assertNotNull(journey.getAutomaticCallTime());
        assertNotNull(journey.getExpectedArrivalTime());

        // Procurement = 11:00 AM, Travel = 45 min, Buffer = 15 min -> Call Time = 10:00 AM
        LocalDateTime expectedProcurement = LocalDateTime.of(booking1.getBookingDate(), LocalTime.of(11, 0));
        LocalDateTime expectedCall = expectedProcurement.minusMinutes(45 + 15);

        assertEquals(expectedCall.getHour(), journey.getAutomaticCallTime().getHour());
        assertEquals(expectedCall.getMinute(), journey.getAutomaticCallTime().getMinute());

        // Simulate Farmer Ready
        VoiceEventPayloadDTO payload = VoiceEventPayloadDTO.builder()
                .bookingId(booking1.getBookingId())
                .farmerId(farmer1.getFarmerId())
                .event(VoiceEventType.FARMER_READY_TO_TRAVEL)
                .source("SIMULATOR")
                .build();

        JourneyMonitoring updated = journeyService.processVoiceEvent(payload);
        assertEquals(JourneyStatus.TRAVELLING, updated.getJourneyStatus());

        // Queue remains WAITING (Zero queue change)
        Booking refreshedBooking = bookingRepository.findById(booking1.getBookingId()).orElseThrow();
        assertEquals(BookingStatus.WAITING, refreshedBooking.getStatus());
    }

    @Test
    @Transactional
    public void testScenario2And3CannotComeCancellationAndOwnerApproval() {
        // Scenario 2: Simulate Cannot Come (Cancellation Requested)
        VoiceEventPayloadDTO payload = VoiceEventPayloadDTO.builder()
                .bookingId(booking1.getBookingId())
                .farmerId(farmer1.getFarmerId())
                .event(VoiceEventType.CANCELLATION_REQUESTED)
                .reason("Cannot attend today due to vehicle breakdown")
                .source("SIMULATOR")
                .build();

        journeyService.processVoiceEvent(payload);

        // Verify Pending Approval Request Created
        List<ApprovalRequestDTO> pending = approvalService.getPendingApprovals("9876543210");
        assertEquals(1, pending.size());
        assertEquals(ApprovalType.CANCELLATION, pending.get(0).getType());
        assertEquals(ApprovalStatus.PENDING, pending.get(0).getStatus());

        // Verify Booking & Queue remain UNCHANGED
        Booking refreshedBooking = bookingRepository.findById(booking1.getBookingId()).orElseThrow();
        assertEquals(BookingStatus.WAITING, refreshedBooking.getStatus());

        // Scenario 3: Owner Approves Cancellation
        ApprovalRequestDTO approved = approvalService.approveRequest(pending.get(0).getId(), "9876543210");
        assertEquals(ApprovalStatus.APPROVED, approved.getStatus());

        // Verify Booking is now CANCELLED
        refreshedBooking = bookingRepository.findById(booking1.getBookingId()).orElseThrow();
        assertEquals(BookingStatus.CANCELLED, refreshedBooking.getStatus());

        // Verify Audit Trail Records
        List<AuditLog> audits = auditRepository.findByBookingIdOrderByTimestampDesc(booking1.getBookingId());
        assertTrue(audits.stream().anyMatch(a -> "OWNER_APPROVED".equals(a.getEventType())));
        assertTrue(audits.stream().anyMatch(a -> "BOOKING_UPDATED".equals(a.getEventType())));
        assertTrue(audits.stream().anyMatch(a -> "QUEUE_RECALCULATED".equals(a.getEventType())));
    }

    @Test
    @Transactional
    public void testScenario4And5RescheduleRequestAndOwnerRejection() {
        // Scenario 4: Simulate Reschedule Request
        VoiceEventPayloadDTO payload = VoiceEventPayloadDTO.builder()
                .bookingId(booking1.getBookingId())
                .farmerId(farmer1.getFarmerId())
                .event(VoiceEventType.RESCHEDULE_REQUESTED)
                .reason("Need alternate slot tomorrow")
                .requestedDate(LocalDate.now().plusDays(1))
                .requestedTime("14:00 PM – 15:00 PM")
                .source("SIMULATOR")
                .build();

        journeyService.processVoiceEvent(payload);

        // Verify Pending Reschedule Request Created
        List<ApprovalRequestDTO> pending = approvalService.getPendingApprovals("9876543210");
        assertEquals(1, pending.size());
        assertEquals(ApprovalType.RESCHEDULE, pending.get(0).getType());

        // Booking remains UNCHANGED
        Booking refreshedBooking = bookingRepository.findById(booking1.getBookingId()).orElseThrow();
        assertEquals(BookingStatus.WAITING, refreshedBooking.getStatus());

        // Scenario 5: Owner Rejects Reschedule Request
        ApprovalRequestDTO rejected = approvalService.rejectRequest(pending.get(0).getId(), "9876543210");
        assertEquals(ApprovalStatus.REJECTED, rejected.getStatus());

        // Booking & Queue remain UNCHANGED
        refreshedBooking = bookingRepository.findById(booking1.getBookingId()).orElseThrow();
        assertEquals(BookingStatus.WAITING, refreshedBooking.getStatus());

        List<AuditLog> audits = auditRepository.findByBookingIdOrderByTimestampDesc(booking1.getBookingId());
        assertTrue(audits.stream().anyMatch(a -> "OWNER_REJECTED".equals(a.getEventType())));
    }

    @Test
    @Transactional
    public void testScenario6TravellingAndMissedArrivalAlert() {
        // Simulate Travelling
        VoiceEventPayloadDTO travelPayload = VoiceEventPayloadDTO.builder()
                .bookingId(booking1.getBookingId())
                .farmerId(farmer1.getFarmerId())
                .event(VoiceEventType.FARMER_READY_TO_TRAVEL)
                .source("SIMULATOR")
                .build();
        journeyService.processVoiceEvent(travelPayload);

        // Simulate Missed Expected Arrival
        VoiceEventPayloadDTO missedPayload = VoiceEventPayloadDTO.builder()
                .bookingId(booking1.getBookingId())
                .farmerId(farmer1.getFarmerId())
                .event(VoiceEventType.MISSED_EXPECTED_ARRIVAL)
                .reason("Farmer missed expected arrival time")
                .source("SYSTEM")
                .build();
        journeyService.processVoiceEvent(missedPayload);

        // Verify Owner Action Required Alert
        List<ActionRequiredDTO> alerts = approvalService.getActionRequiredAlerts("9876543210");
        assertFalse(alerts.isEmpty());
        assertTrue(alerts.stream().anyMatch(a -> a.getBookingId().equals(booking1.getBookingId()) && a.getIssueType() == VoiceEventType.MISSED_EXPECTED_ARRIVAL));

        // CRITICAL CHECKPOINT: Booking is NOT automatically cancelled!
        Booking refreshedBooking = bookingRepository.findById(booking1.getBookingId()).orElseThrow();
        assertNotEquals(BookingStatus.CANCELLED, refreshedBooking.getStatus());
        assertEquals(BookingStatus.WAITING, refreshedBooking.getStatus());
    }
}
