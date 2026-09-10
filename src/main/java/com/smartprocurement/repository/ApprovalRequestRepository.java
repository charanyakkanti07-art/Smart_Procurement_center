package com.smartprocurement.repository;

import com.smartprocurement.entity.ApprovalRequest;
import com.smartprocurement.entity.ApprovalStatus;
import com.smartprocurement.entity.ApprovalType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApprovalRequestRepository extends JpaRepository<ApprovalRequest, Long> {

    List<ApprovalRequest> findByCentreCentreIdAndStatus(Long centreId, ApprovalStatus status);

    List<ApprovalRequest> findByCentreCentreId(Long centreId);

    Optional<ApprovalRequest> findByBookingBookingIdAndTypeAndStatus(Long bookingId, ApprovalType type, ApprovalStatus status);

    List<ApprovalRequest> findByBookingBookingId(Long bookingId);
}
