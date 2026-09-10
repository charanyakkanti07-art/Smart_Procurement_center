package com.smartprocurement.repository;

import com.smartprocurement.entity.Procurement;
import com.smartprocurement.entity.ProcurementStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProcurementRepository extends JpaRepository<Procurement, Long> {
    Optional<Procurement> findByBookingBookingId(Long bookingId);
    List<Procurement> findByFarmerFarmerIdOrderByCreatedAtDesc(Long farmerId);
    List<Procurement> findByCentreCentreIdOrderByCreatedAtDesc(Long centreId);
    List<Procurement> findByCentreCentreIdAndStatus(Long centreId, ProcurementStatus status);
    Optional<Procurement> findByProcurementCode(String procurementCode);
}
