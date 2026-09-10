package com.smartprocurement.repository;

import com.smartprocurement.entity.Notification;
import com.smartprocurement.entity.NotificationEventType;
import com.smartprocurement.entity.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByFarmerFarmerIdOrderByCreatedAtDesc(Long farmerId);

    long countByFarmerFarmerIdAndReadAtIsNull(Long farmerId);

    List<Notification> findByFarmerFarmerIdAndReadAtIsNull(Long farmerId);

    List<Notification> findByFarmerFarmerIdAndEventTypeAndReferenceId(Long farmerId, NotificationEventType eventType, String referenceId);
}
