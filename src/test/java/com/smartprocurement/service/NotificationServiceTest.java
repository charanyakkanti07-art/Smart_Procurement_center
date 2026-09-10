package com.smartprocurement.service;

import com.smartprocurement.dto.NotificationDTO;
import com.smartprocurement.dto.NotificationPreferenceDTO;
import com.smartprocurement.entity.*;
import com.smartprocurement.repository.FarmerRepository;
import com.smartprocurement.repository.NotificationPreferenceRepository;
import com.smartprocurement.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class NotificationServiceTest {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationPreferenceRepository preferenceRepository;

    @Autowired
    private FarmerRepository farmerRepository;

    private Farmer testFarmer;

    @BeforeEach
    @Transactional
    public void setup() {
        notificationRepository.deleteAll();
        preferenceRepository.deleteAll();

        testFarmer = farmerRepository.save(Farmer.builder()
                .name("Farmer Ramesh")
                .phone("9876543210")
                .language("Telugu")
                .build());
    }

    @Test
    @Transactional
    public void testNotificationPreferencesAndChannelSelection() {
        NotificationPreferenceDTO pref = notificationService.getPreferences(testFarmer.getFarmerId());
        assertNotNull(pref);
        assertTrue(pref.isAppEnabled());
        assertTrue(pref.isSmsEnabled());
        assertTrue(pref.isVoiceEnabled());
        assertEquals("Telugu", pref.getLanguage());

        // Update preferences to English and disable SMS
        pref.setSmsEnabled(false);
        pref.setLanguage("English");
        NotificationPreferenceDTO updated = notificationService.updatePreferences(testFarmer.getFarmerId(), pref);

        assertFalse(updated.isSmsEnabled());
        assertEquals("English", updated.getLanguage());

        // Send booking confirmation notification
        Map<String, Object> params = new HashMap<>();
        params.put("centreName", "Centre A");
        params.put("bookingDate", "2026-09-20");
        params.put("slot", "Morning Slot");

        NotificationDTO dto = notificationService.sendNotification(
                testFarmer.getFarmerId(),
                NotificationEventType.BOOKING_CONFIRMATION,
                params,
                "ref_confirm_1"
        );

        assertNotNull(dto);
        assertEquals("Booking Confirmed", dto.getTitle());
        assertTrue(dto.getMessage().contains("Centre A"));

        List<Notification> allNotifs = notificationRepository.findByFarmerFarmerIdOrderByCreatedAtDesc(testFarmer.getFarmerId());
        // APP and VOICE records generated (SMS was disabled)
        assertTrue(allNotifs.stream().anyMatch(n -> n.getChannel() == NotificationChannel.APP));
        assertTrue(allNotifs.stream().anyMatch(n -> n.getChannel() == NotificationChannel.VOICE));
        assertFalse(allNotifs.stream().anyMatch(n -> n.getChannel() == NotificationChannel.SMS));
    }

    @Test
    @Transactional
    public void testMultilingualTemplateRendering() {
        // Test Hindi
        testFarmer.setLanguage("Hindi");
        farmerRepository.save(testFarmer);
        preferenceRepository.deleteAll(); // recreate preference

        Map<String, Object> params = new HashMap<>();
        params.put("oldPosition", 8);
        params.put("newPosition", 5);
        params.put("estimatedWaitMinutes", 25);

        NotificationDTO dtoHindi = notificationService.sendNotification(
                testFarmer.getFarmerId(),
                NotificationEventType.QUEUE_CHANGED,
                params,
                "ref_q_hindi"
        );

        assertNotNull(dtoHindi);
        assertEquals("कतार की स्थिति बदली", dtoHindi.getTitle());
        assertTrue(dtoHindi.getMessage().contains("8 से बदलकर 5"));

        // Test Phase 13 ISO language code "te" for Telugu
        testFarmer.setLanguage("te");
        farmerRepository.save(testFarmer);
        preferenceRepository.deleteAll();

        NotificationDTO dtoTe = notificationService.sendNotification(
                testFarmer.getFarmerId(),
                NotificationEventType.QUEUE_CHANGED,
                params,
                "ref_q_te"
        );
        assertNotNull(dtoTe);
        assertEquals("క్యూ స్థానం మారింది", dtoTe.getTitle());

        // Test Phase 13 ISO language code "hi" for Hindi
        testFarmer.setLanguage("hi");
        farmerRepository.save(testFarmer);
        preferenceRepository.deleteAll();

        NotificationDTO dtoHi = notificationService.sendNotification(
                testFarmer.getFarmerId(),
                NotificationEventType.QUEUE_CHANGED,
                params,
                "ref_q_hi"
        );
        assertNotNull(dtoHi);
        assertEquals("कतार की स्थिति बदली", dtoHi.getTitle());
    }

    @Test
    @Transactional
    public void testReadUnreadState() {
        Map<String, Object> params = new HashMap<>();
        params.put("centreName", "Centre A");

        NotificationDTO dto = notificationService.sendNotification(
                testFarmer.getFarmerId(),
                NotificationEventType.SLOT_REMINDER,
                params,
                "ref_read_test"
        );

        long unread = notificationService.getUnreadCount(testFarmer.getFarmerId());
        assertTrue(unread > 0);

        NotificationDTO readDto = notificationService.markAsRead(dto.getNotificationId(), testFarmer.getFarmerId());
        assertEquals(NotificationStatus.READ, readDto.getStatus());
        assertNotNull(readDto.getReadAt());

        notificationService.markAllAsRead(testFarmer.getFarmerId());
        assertEquals(0, notificationService.getUnreadCount(testFarmer.getFarmerId()));
    }
}
