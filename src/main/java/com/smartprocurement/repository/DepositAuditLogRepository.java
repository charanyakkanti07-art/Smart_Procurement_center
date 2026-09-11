package com.smartprocurement.repository;

import com.smartprocurement.entity.DepositAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepositAuditLogRepository extends JpaRepository<DepositAuditLog, Long> {

    List<DepositAuditLog> findByBookingIdOrderByCreatedAtDesc(Long bookingId);

    List<DepositAuditLog> findByFarmerIdOrderByCreatedAtDesc(Long farmerId);

    List<DepositAuditLog> findTop50ByOrderByCreatedAtDesc();
}
