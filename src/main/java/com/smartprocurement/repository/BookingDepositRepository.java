package com.smartprocurement.repository;

import com.smartprocurement.entity.BookingDeposit;
import com.smartprocurement.entity.DepositStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingDepositRepository extends JpaRepository<BookingDeposit, Long> {

    Optional<BookingDeposit> findByBookingId(Long bookingId);

    List<BookingDeposit> findByFarmerId(Long farmerId);

    List<BookingDeposit> findByProcurementCentreId(Long procurementCentreId);

    List<BookingDeposit> findByDepositStatus(DepositStatus depositStatus);

    Optional<BookingDeposit> findByPaymentTransactionId(String paymentTransactionId);

    @Query("SELECT COUNT(d) FROM BookingDeposit d WHERE d.depositStatus = :status")
    Long countByDepositStatus(DepositStatus status);

    @Query("SELECT COALESCE(SUM(d.depositAmount), 0.0) FROM BookingDeposit d WHERE d.depositStatus = 'PAID'")
    Double sumPaidDeposits();

    @Query("SELECT COALESCE(SUM(d.depositAmount), 0.0) FROM BookingDeposit d WHERE d.depositStatus = 'REFUNDED'")
    Double sumRefundedDeposits();

    @Query("SELECT COALESCE(SUM(d.depositAmount), 0.0) FROM BookingDeposit d WHERE d.depositStatus = 'FORFEITED'")
    Double sumForfeitedDeposits();

    @Query("SELECT COALESCE(SUM(d.depositAmount), 0.0) FROM BookingDeposit d WHERE d.depositStatus = 'REFUND_PENDING'")
    Double sumPendingRefundDeposits();
}
