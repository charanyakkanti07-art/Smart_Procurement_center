package com.smartprocurement.service;

import com.smartprocurement.dto.NotificationDTO;
import com.smartprocurement.dto.NotificationPreferenceDTO;
import com.smartprocurement.entity.*;
import com.smartprocurement.exception.ResourceNotFoundException;
import com.smartprocurement.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class NotificationE2ETest {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private QueueService queueService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private OwnerService ownerService;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationPreferenceRepository preferenceRepository;

    @Autowired
    private FarmerRepository farmerRepository;

    @Autowired
    private CentreRepository centreRepository;

    @Autowired
    private CropRepository cropRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private Farmer farmer101;
    private ProcurementCentre centreA;
    private ProcurementCentre centreB;
    private Crop crop1;
    private Booking booking1;

    @BeforeEach
    @Transactional
    public void setup() {
        notificationRepository.deleteAll();
        preferenceRepository.deleteAll();
        bookingRepository.deleteAll();
        cropRepository.deleteAll();
        farmerRepository.deleteAll();
        centreRepository.deleteAll();

        // 1. Create farmer #101
        farmer101 = farmerRepository.save(Farmer.builder()
                .name("Farmer 101")
                .phone("9876543210")
                .language("English")
                .build());

        centreA = centreRepository.save(ProcurementCentre.builder()
                .name("Centre A")
                .location("Medak")
                .totalCapacity(10000.0)
                .currentLoad(0.0)
                .status(CentreStatus.ACTIVE)
                .build());

        centreB = centreRepository.save(ProcurementCentre.builder()
                .name("Centre B")
                .location("Sangareddy")
                .totalCapacity(10000.0)
                .currentLoad(0.0)
                .status(CentreStatus.ACTIVE)
                .build());

        crop1 = cropRepository.save(Crop.builder()
                .cropType("Paddy")
                .quantity(500.0)
                .farmer(farmer101)
                .build());

        booking1 = bookingRepository.save(Booking.builder()
                .farmer(farmer101)
                .centre(centreA)
                .crop(crop1)
                .quantity(100.0)
                .bookingDate(LocalDate.now())
                .slot("Morning Slot")
                .status(BookingStatus.WAITING)
                .build());
    }

    @Test
    @Transactional
    public void testMandatory12StepEndToEndQueuePositionChangeAndIdempotency() {
        // Step 1: Farmer #101 already created in setup()
        assertNotNull(farmer101.getFarmerId());

        // Step 2 & 3: Give farmer queue position #8 and change queue position to #5
        int oldPos = 8;
        int newPos = 5;

        queueService.updateQueuePositionAndNotify(
                farmer101.getFarmerId(),
                oldPos,
                newPos,
                booking1.getBookingId(),
                centreA.getName(),
                25
        );

        // Step 4: Verify QUEUE_CHANGED event
        List<Notification> records = notificationRepository.findByFarmerFarmerIdOrderByCreatedAtDesc(farmer101.getFarmerId());
        assertFalse(records.isEmpty());
        assertTrue(records.stream().anyMatch(n -> n.getEventType() == NotificationEventType.QUEUE_CHANGED));

        // Step 5: Verify notification database record
        Notification dbRecord = records.stream()
                .filter(n -> n.getEventType() == NotificationEventType.QUEUE_CHANGED && n.getChannel() == NotificationChannel.APP)
                .findFirst()
                .orElse(null);
        assertNotNull(dbRecord);

        // Step 6: Verify correct farmer_id
        assertEquals(farmer101.getFarmerId(), dbRecord.getFarmer().getFarmerId());

        // Step 7: Verify correct old/new position in message
        assertTrue(dbRecord.getMessage().contains("from 8 to 5") || dbRecord.getMessage().contains("8 to 5"));

        // Step 8: Verify app notification
        assertEquals(NotificationChannel.APP, dbRecord.getChannel());
        assertEquals(NotificationStatus.SENT, dbRecord.getStatus());

        // Step 9 & 10: Verify SMS and Voice mock records created
        assertTrue(records.stream().anyMatch(n -> n.getChannel() == NotificationChannel.SMS));
        assertTrue(records.stream().anyMatch(n -> n.getChannel() == NotificationChannel.VOICE));

        int initialCount = notificationRepository.findAll().size();

        // Step 11: Process the same event again (duplicate trigger)
        queueService.updateQueuePositionAndNotify(
                farmer101.getFarmerId(),
                oldPos,
                newPos,
                booking1.getBookingId(),
                centreA.getName(),
                25
        );

        // Step 12: Verify NO duplicate notification created (Idempotency)
        int updatedCount = notificationRepository.findAll().size();
        assertEquals(initialCount, updatedCount);
    }

    @Test
    @Transactional
    public void testCriticalRuleNoStateChangeNoNotification() {
        int initialCount = notificationRepository.findAll().size();

        // Trigger queue position update from 8 to 8 (NO state change)
        queueService.updateQueuePositionAndNotify(
                farmer101.getFarmerId(),
                8,
                8,
                booking1.getBookingId(),
                centreA.getName(),
                25
        );

        // Verify NO notification created
        assertEquals(initialCount, notificationRepository.findAll().size());

        // Trigger centre change from Centre A to Centre A (NO state change)
        ownerService.changeCentreAndNotify(booking1.getBookingId(), centreA.getCentreId());

        // Verify NO notification created
        assertEquals(initialCount, notificationRepository.findAll().size());
    }

    @Test
    @Transactional
    public void testAllBusinessEventsIntegration() {
        Long fId = farmer101.getFarmerId();

        // 1. BOOKING_CONFIRMATION
        Map<String, Object> p1 = new HashMap<>();
        p1.put("centreName", "Centre A");
        p1.put("slot", "Morning");
        notificationService.sendNotification(fId, NotificationEventType.BOOKING_CONFIRMATION, p1, "ref_1");

        // 2. SLOT_REMINDER
        Map<String, Object> p2 = new HashMap<>();
        p2.put("slot", "Morning");
        notificationService.sendNotification(fId, NotificationEventType.SLOT_REMINDER, p2, "ref_2");

        // 3. QUEUE_APPROACHING
        Map<String, Object> p3 = new HashMap<>();
        p3.put("queuePosition", 2);
        notificationService.sendNotification(fId, NotificationEventType.QUEUE_APPROACHING, p3, "ref_3");

        // 4. START_TRAVELLING
        Map<String, Object> p4 = new HashMap<>();
        p4.put("centreName", "Centre A");
        notificationService.sendNotification(fId, NotificationEventType.START_TRAVELLING, p4, "ref_4");

        // 5. QUEUE_CHANGED
        Map<String, Object> p5 = new HashMap<>();
        p5.put("oldPosition", 8);
        p5.put("newPosition", 5);
        notificationService.sendNotification(fId, NotificationEventType.QUEUE_CHANGED, p5, "ref_5");

        // 6. CENTRE_CHANGED
        ownerService.changeCentreAndNotify(booking1.getBookingId(), centreB.getCentreId());

        // 7. RESCHEDULE_REQUEST
        Map<String, Object> p7 = new HashMap<>();
        p7.put("slot", "Afternoon");
        notificationService.sendNotification(fId, NotificationEventType.RESCHEDULE_REQUEST, p7, "ref_7");

        // 8. CANCELLATION_REQUEST
        Map<String, Object> p8 = new HashMap<>();
        notificationService.sendNotification(fId, NotificationEventType.CANCELLATION_REQUEST, p8, "ref_8");

        // 9. OWNER_APPROVAL
        Map<String, Object> p9 = new HashMap<>();
        p9.put("action", "approved");
        notificationService.sendNotification(fId, NotificationEventType.OWNER_APPROVAL, p9, "ref_9");

        // 10. PROCUREMENT_COMPLETED
        Map<String, Object> p10 = new HashMap<>();
        p10.put("quantity", 500.0);
        p10.put("cropType", "Paddy");
        notificationService.sendNotification(fId, NotificationEventType.PROCUREMENT_COMPLETED, p10, "ref_10");

        // 11. PAYMENT_COMPLETED
        ownerService.completePaymentAndNotify(booking1.getBookingId(), 25000.0, "TXN-999");

        List<NotificationDTO> notifs = notificationService.getFarmerNotifications(fId);
        assertFalse(notifs.isEmpty());
        assertTrue(notifs.stream().anyMatch(n -> n.getEventType() == NotificationEventType.PAYMENT_COMPLETED));
        assertTrue(notifs.stream().anyMatch(n -> n.getEventType() == NotificationEventType.CENTRE_CHANGED));
    }

    @Test
    @Transactional
    public void testUnauthorizedAccessAndValidation() {
        Farmer otherFarmer = farmerRepository.save(Farmer.builder().name("Other").phone("9111111111").build());

        Map<String, Object> params = new HashMap<>();
        params.put("centreName", "Centre A");
        NotificationDTO dto = notificationService.sendNotification(farmer101.getFarmerId(), NotificationEventType.BOOKING_CONFIRMATION, params, "ref_unauth");

        // Trying to mark notification as read with wrong farmerId throws Exception
        assertThrows(ResourceNotFoundException.class, () -> {
            notificationService.markAsRead(dto.getNotificationId(), otherFarmer.getFarmerId());
        });
    }
}
