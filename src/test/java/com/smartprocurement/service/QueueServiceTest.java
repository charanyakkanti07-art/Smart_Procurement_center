package com.smartprocurement.service;

import com.smartprocurement.dto.QueueStatusDTO;
import com.smartprocurement.entity.*;
import com.smartprocurement.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class QueueServiceTest {

    @Autowired
    private QueueService queueService;

    @Autowired
    private QueueEntryRepository queueEntryRepository;

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
    private Farmer farmer1, farmer2, farmer3, farmer4;
    private Crop crop;
    private Booking b1, b2, b3, b4;

    @BeforeEach
    @Transactional
    public void setup() {
        queueEntryRepository.deleteAll();
        bookingRepository.deleteAll();
        cropRepository.deleteAll();
        farmerRepository.deleteAll();
        userRepository.deleteAll();
        centreRepository.deleteAll();

        centre = centreRepository.save(ProcurementCentre.builder()
                .name("Test Procurement Centre")
                .location("Hyderabad")
                .totalCapacity(5000.0)
                .currentLoad(0.0)
                .status(CentreStatus.ACTIVE)
                .build());

        User owner = User.builder()
                .phone("9876543210")
                .name("Owner Ramu")
                .password("password123")
                .role(Role.OWNER)
                .centre(centre)
                .build();
        userRepository.save(owner);

        farmer1 = farmerRepository.save(Farmer.builder().name("Farmer A").phone("9000000001").build());
        farmer2 = farmerRepository.save(Farmer.builder().name("Farmer B").phone("9000000002").build());
        farmer3 = farmerRepository.save(Farmer.builder().name("Farmer C").phone("9000000003").build());
        farmer4 = farmerRepository.save(Farmer.builder().name("Farmer D").phone("9000000004").build());

        crop = cropRepository.save(Crop.builder().cropType("Paddy").quantity(500.0).farmer(farmer1).build());

        b1 = bookingRepository.save(Booking.builder().farmer(farmer1).centre(centre).crop(crop).quantity(100.0).bookingDate(LocalDate.now()).slot("10:00 AM").status(BookingStatus.WAITING).arrivalTime(LocalDateTime.now().minusMinutes(30)).build());
        b2 = bookingRepository.save(Booking.builder().farmer(farmer2).centre(centre).crop(crop).quantity(100.0).bookingDate(LocalDate.now()).slot("10:00 AM").status(BookingStatus.WAITING).arrivalTime(LocalDateTime.now().minusMinutes(20)).build());
        b3 = bookingRepository.save(Booking.builder().farmer(farmer3).centre(centre).crop(crop).quantity(100.0).bookingDate(LocalDate.now()).slot("10:00 AM").status(BookingStatus.WAITING).arrivalTime(LocalDateTime.now().minusMinutes(10)).build());
        b4 = bookingRepository.save(Booking.builder().farmer(farmer4).centre(centre).crop(crop).quantity(100.0).bookingDate(LocalDate.now()).slot("10:00 AM").status(BookingStatus.WAITING).arrivalTime(LocalDateTime.now().minusMinutes(5)).build());
    }

    @Test
    @Transactional
    public void testPhase6QueueWorkflow() {
        // TEST 1: Four queue entries created. Call next -> #101 PROCESSING, #102 WAITING, #103 WAITING, #104 WAITING
        QueueStatusDTO called1 = queueService.callNextFarmer(centre.getCentreId(), "9876543210");
        assertEquals(b1.getBookingId(), called1.getBookingId());
        assertEquals(BookingStatus.PROCESSING, called1.getStatus());

        List<QueueStatusDTO> queue = queueService.getQueue(centre.getCentreId());
        assertEquals(4, queue.size());

        QueueStatusDTO q1 = queueService.getFarmerQueueStatus(b1.getBookingId());
        QueueStatusDTO q2 = queueService.getFarmerQueueStatus(b2.getBookingId());
        QueueStatusDTO q3 = queueService.getFarmerQueueStatus(b3.getBookingId());
        QueueStatusDTO q4 = queueService.getFarmerQueueStatus(b4.getBookingId());

        assertEquals(BookingStatus.PROCESSING, q1.getStatus());
        assertEquals(BookingStatus.WAITING, q2.getStatus());
        assertEquals(BookingStatus.WAITING, q3.getStatus());
        assertEquals(BookingStatus.WAITING, q4.getStatus());

        assertEquals(1, q2.getQueuePosition());
        assertEquals(0, q2.getFarmersAhead());

        assertEquals(2, q3.getQueuePosition());
        assertEquals(1, q3.getFarmersAhead());

        assertEquals(3, q4.getQueuePosition());
        assertEquals(2, q4.getFarmersAhead());

        // TEST 2: Complete #101 -> #102 PROCESSING, #103 WAITING, #104 WAITING automatically
        QueueStatusDTO comp1 = queueService.completeProcurement(b1.getBookingId(), "9876543210");
        assertEquals(BookingStatus.COMPLETED, comp1.getStatus());

        q2 = queueService.getFarmerQueueStatus(b2.getBookingId());
        q3 = queueService.getFarmerQueueStatus(b3.getBookingId());
        q4 = queueService.getFarmerQueueStatus(b4.getBookingId());

        assertEquals(BookingStatus.PROCESSING, q2.getStatus());
        assertEquals(BookingStatus.WAITING, q3.getStatus());
        assertEquals(BookingStatus.WAITING, q4.getStatus());

        // TEST 3 & CRITICAL CHECKPOINT: Cancel #103 with owner approval -> #102 PROCESSING, #103 CANCELLED, #104 WAITING
        queueService.requestCancellation(b3.getBookingId());
        QueueStatusDTO cancelApproved = queueService.approveCancellation(b3.getBookingId(), "9876543210");
        assertEquals(BookingStatus.CANCELLED, cancelApproved.getStatus());

        // TEST 4 & TEST 5: Verify queue positions and farmers ahead
        q2 = queueService.getFarmerQueueStatus(b2.getBookingId());
        q3 = queueService.getFarmerQueueStatus(b3.getBookingId());
        q4 = queueService.getFarmerQueueStatus(b4.getBookingId());

        assertEquals(BookingStatus.PROCESSING, q2.getStatus());
        assertEquals(1, q2.getQueuePosition());
        assertEquals(0, q2.getFarmersAhead());

        assertEquals(BookingStatus.CANCELLED, q3.getStatus());
        assertEquals(0, q3.getQueuePosition());

        // CRITICAL CHECKPOINT: #104 automatically moves up!
        assertEquals(1, q4.getQueuePosition());
        assertEquals(0, q4.getFarmersAhead());

        // TEST 6: Verify estimated wait time calculation
        assertNotNull(q4.getEstimatedWaitFormatted());
    }

    @Test
    @Transactional
    public void testConcurrencyCallNextRejection() {
        queueService.callNextFarmer(centre.getCentreId(), "9876543210");
        // Attempting to call next while one is processing should throw exception
        assertThrows(RuntimeException.class, () -> {
            queueService.callNextFarmer(centre.getCentreId(), "9876543210");
        });
    }
}
