package com.smartprocurement.repository;

import com.smartprocurement.entity.Payment;
import com.smartprocurement.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByProcurementProcurementId(Long procurementId);
    List<Payment> findByFarmerFarmerIdOrderByCreatedAtDesc(Long farmerId);
    Optional<Payment> findByTransactionId(String transactionId);
    List<Payment> findByStatus(PaymentStatus status);
    List<Payment> findByProcurementCentreCentreIdOrderByCreatedAtDesc(Long centreId);
}
