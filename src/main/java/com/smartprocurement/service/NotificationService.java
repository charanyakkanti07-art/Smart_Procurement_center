package com.smartprocurement.service;

import com.smartprocurement.dto.NotificationDTO;
import com.smartprocurement.dto.NotificationPreferenceDTO;
import com.smartprocurement.entity.*;
import com.smartprocurement.exception.ResourceNotFoundException;
import com.smartprocurement.repository.FarmerRepository;
import com.smartprocurement.repository.NotificationPreferenceRepository;
import com.smartprocurement.repository.NotificationRepository;
import com.smartprocurement.service.provider.SmsProvider;
import com.smartprocurement.service.provider.VoiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationPreferenceRepository preferenceRepository;

    @Autowired
    private FarmerRepository farmerRepository;

    @Autowired
    private NotificationTemplateEngine templateEngine;

    @Autowired
    private SmsProvider smsProvider;

    @Autowired
    private VoiceProvider voiceProvider;

    @Autowired(required = false)
    private SocketService socketService;

    @Transactional
    public NotificationDTO sendNotification(Long farmerId, NotificationEventType eventType, Map<String, Object> params, String referenceId) {
        Farmer farmer = farmerRepository.findById(farmerId)
                .orElseThrow(() -> new ResourceNotFoundException("Farmer not found with id: " + farmerId));

        // IDEMPOTENCY CHECK: Prevent duplicate notifications for exact same farmer, eventType, and referenceId
        if (referenceId != null && !referenceId.trim().isEmpty()) {
            List<Notification> existing = notificationRepository.findByFarmerFarmerIdAndEventTypeAndReferenceId(farmerId, eventType, referenceId);
            if (!existing.isEmpty()) {
                logger.info("Idempotency check triggered: Notification already processed for farmerId={}, eventType={}, referenceId={}",
                        farmerId, eventType, referenceId);
                return mapToDTO(existing.get(0));
            }
        }

        NotificationPreference preference = preferenceRepository.findByFarmerFarmerId(farmerId)
                .orElseGet(() -> {
                    NotificationPreference defaultPref = NotificationPreference.builder()
                            .farmer(farmer)
                            .appEnabled(true)
                            .smsEnabled(true)
                            .voiceEnabled(true)
                            .language(farmer.getLanguage() != null ? farmer.getLanguage() : "Telugu")
                            .build();
                    return preferenceRepository.save(defaultPref);
                });

        String lang = preference.getLanguage() != null ? preference.getLanguage() : "Telugu";
        NotificationTemplateEngine.TemplateResult template = templateEngine.render(eventType, lang, params);

        List<Notification> createdNotifications = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        // 1. APP Channel Notification
        if (preference.isAppEnabled()) {
            Notification appNotif = Notification.builder()
                    .farmer(farmer)
                    .eventType(eventType)
                    .title(template.getTitle())
                    .message(template.getMessage())
                    .channel(NotificationChannel.APP)
                    .status(NotificationStatus.SENT)
                    .referenceId(referenceId)
                    .sentAt(now)
                    .build();
            Notification savedApp = notificationRepository.save(appNotif);
            createdNotifications.add(savedApp);

            if (socketService != null) {
                socketService.broadcastNotification(farmerId, mapToDTO(savedApp));
            }
        }

        // 2. SMS Channel Notification
        if (preference.isSmsEnabled()) {
            NotificationStatus status = NotificationStatus.SENT;
            try {
                boolean success = smsProvider.sendSms(farmer.getPhone(), template.getMessage(), eventType);
                if (!success) {
                    status = NotificationStatus.FAILED;
                }
            } catch (Exception e) {
                logger.error("Failed to dispatch SMS notification to {}", farmer.getPhone(), e);
                status = NotificationStatus.FAILED;
            }

            Notification smsNotif = Notification.builder()
                    .farmer(farmer)
                    .eventType(eventType)
                    .title(template.getTitle())
                    .message(template.getMessage())
                    .channel(NotificationChannel.SMS)
                    .status(status)
                    .referenceId(referenceId)
                    .sentAt(now)
                    .build();
            createdNotifications.add(notificationRepository.save(smsNotif));
        }

        // 3. VOICE Channel Notification
        if (preference.isVoiceEnabled()) {
            NotificationStatus status = NotificationStatus.SENT;
            try {
                boolean success = voiceProvider.sendVoiceCall(farmer.getPhone(), template.getMessage(), eventType);
                if (!success) {
                    status = NotificationStatus.FAILED;
                }
            } catch (Exception e) {
                logger.error("Failed to dispatch Voice notification to {}", farmer.getPhone(), e);
                status = NotificationStatus.FAILED;
            }

            Notification voiceNotif = Notification.builder()
                    .farmer(farmer)
                    .eventType(eventType)
                    .title(template.getTitle())
                    .message(template.getMessage())
                    .channel(NotificationChannel.VOICE)
                    .status(status)
                    .referenceId(referenceId)
                    .sentAt(now)
                    .build();
            createdNotifications.add(notificationRepository.save(voiceNotif));
        }

        if (createdNotifications.isEmpty()) {
            // Fallback: Save APP notification if all channels were disabled
            Notification defaultNotif = Notification.builder()
                    .farmer(farmer)
                    .eventType(eventType)
                    .title(template.getTitle())
                    .message(template.getMessage())
                    .channel(NotificationChannel.APP)
                    .status(NotificationStatus.SENT)
                    .referenceId(referenceId)
                    .sentAt(now)
                    .build();
            Notification saved = notificationRepository.save(defaultNotif);
            return mapToDTO(saved);
        }

        // Return the APP notification or the first created record
        Notification primary = createdNotifications.stream()
                .filter(n -> n.getChannel() == NotificationChannel.APP)
                .findFirst()
                .orElse(createdNotifications.get(0));

        return mapToDTO(primary);
    }

    @Transactional(readOnly = true)
    public List<NotificationDTO> getFarmerNotifications(Long farmerId) {
        if (!farmerRepository.existsById(farmerId)) {
            throw new ResourceNotFoundException("Farmer not found with id: " + farmerId);
        }
        return notificationRepository.findByFarmerFarmerIdOrderByCreatedAtDesc(farmerId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(Long farmerId) {
        if (!farmerRepository.existsById(farmerId)) {
            throw new ResourceNotFoundException("Farmer not found with id: " + farmerId);
        }
        return notificationRepository.countByFarmerFarmerIdAndReadAtIsNull(farmerId);
    }

    @Transactional
    public NotificationDTO markAsRead(Long notificationId, Long farmerId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + notificationId));

        if (!notification.getFarmer().getFarmerId().equals(farmerId)) {
            throw new ResourceNotFoundException("Unauthorized: Notification does not belong to farmer " + farmerId);
        }

        if (notification.getReadAt() == null) {
            notification.setReadAt(LocalDateTime.now());
            notification.setStatus(NotificationStatus.READ);
            notification = notificationRepository.save(notification);
        }

        return mapToDTO(notification);
    }

    @Transactional
    public void markAllAsRead(Long farmerId) {
        if (!farmerRepository.existsById(farmerId)) {
            throw new ResourceNotFoundException("Farmer not found with id: " + farmerId);
        }
        List<Notification> unread = notificationRepository.findByFarmerFarmerIdAndReadAtIsNull(farmerId);
        LocalDateTime now = LocalDateTime.now();
        for (Notification n : unread) {
            n.setReadAt(now);
            n.setStatus(NotificationStatus.READ);
        }
        notificationRepository.saveAll(unread);
    }

    @Transactional
    public NotificationPreferenceDTO getPreferences(Long farmerId) {
        Farmer farmer = farmerRepository.findById(farmerId)
                .orElseThrow(() -> new ResourceNotFoundException("Farmer not found with id: " + farmerId));

        NotificationPreference preference = preferenceRepository.findByFarmerFarmerId(farmerId)
                .orElseGet(() -> {
                    NotificationPreference defaultPref = NotificationPreference.builder()
                            .farmer(farmer)
                            .appEnabled(true)
                            .smsEnabled(true)
                            .voiceEnabled(true)
                            .language(farmer.getLanguage() != null ? farmer.getLanguage() : "Telugu")
                            .build();
                    return preferenceRepository.save(defaultPref);
                });

        return mapPreferenceToDTO(preference);
    }

    @Transactional
    public NotificationPreferenceDTO updatePreferences(Long farmerId, NotificationPreferenceDTO dto) {
        Farmer farmer = farmerRepository.findById(farmerId)
                .orElseThrow(() -> new ResourceNotFoundException("Farmer not found with id: " + farmerId));

        NotificationPreference preference = preferenceRepository.findByFarmerFarmerId(farmerId)
                .orElseGet(() -> NotificationPreference.builder().farmer(farmer).build());

        preference.setAppEnabled(dto.isAppEnabled());
        preference.setSmsEnabled(dto.isSmsEnabled());
        preference.setVoiceEnabled(dto.isVoiceEnabled());
        if (dto.getLanguage() != null && !dto.getLanguage().trim().isEmpty()) {
            preference.setLanguage(dto.getLanguage().trim());
            farmer.setLanguage(dto.getLanguage().trim());
            farmerRepository.save(farmer);
        }

        NotificationPreference updated = preferenceRepository.save(preference);
        return mapPreferenceToDTO(updated);
    }

    public NotificationDTO mapToDTO(Notification n) {
        return NotificationDTO.builder()
                .notificationId(n.getNotificationId())
                .farmerId(n.getFarmer() != null ? n.getFarmer().getFarmerId() : null)
                .eventType(n.getEventType())
                .title(n.getTitle())
                .message(n.getMessage())
                .channel(n.getChannel())
                .status(n.getStatus())
                .referenceId(n.getReferenceId())
                .createdAt(n.getCreatedAt())
                .sentAt(n.getSentAt())
                .readAt(n.getReadAt())
                .build();
    }

    private NotificationPreferenceDTO mapPreferenceToDTO(NotificationPreference p) {
        return NotificationPreferenceDTO.builder()
                .farmerId(p.getFarmer() != null ? p.getFarmer().getFarmerId() : null)
                .appEnabled(p.isAppEnabled())
                .smsEnabled(p.isSmsEnabled())
                .voiceEnabled(p.isVoiceEnabled())
                .language(p.getLanguage())
                .build();
    }
}
