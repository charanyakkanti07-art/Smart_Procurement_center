package com.smartprocurement.repository;

import com.smartprocurement.entity.BookingStatus;
import com.smartprocurement.entity.QueueEntry;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QueueEntryRepository extends JpaRepository<QueueEntry, Long> {

    Optional<QueueEntry> findByBookingBookingId(Long bookingId);

    List<QueueEntry> findByCentreCentreId(Long centreId);

    List<QueueEntry> findByFarmerFarmerId(Long farmerId);

    List<QueueEntry> findByCentreCentreIdAndStatusInOrderByArrivalTimeAscCreatedAtAsc(Long centreId, List<BookingStatus> statuses);

    long countByCentreCentreIdAndStatusIn(Long centreId, List<BookingStatus> statuses);

    Optional<QueueEntry> findFirstByCentreCentreIdAndStatus(Long centreId, BookingStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT q FROM QueueEntry q WHERE q.centre.centreId = :centreId AND q.status = :status")
    Optional<QueueEntry> findFirstByCentreIdAndStatusWithLock(@Param("centreId") Long centreId, @Param("status") BookingStatus status);

    @Query("SELECT q FROM QueueEntry q WHERE q.centre.centreId = :centreId AND q.status = 'COMPLETED' AND q.processingStartTime IS NOT NULL AND q.processingEndTime IS NOT NULL ORDER BY q.processingEndTime DESC")
    List<QueueEntry> findRecentCompletedEntries(@Param("centreId") Long centreId, Pageable pageable);
}
