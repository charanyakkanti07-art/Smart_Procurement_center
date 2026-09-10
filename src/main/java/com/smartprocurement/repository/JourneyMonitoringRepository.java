package com.smartprocurement.repository;

import com.smartprocurement.entity.JourneyMonitoring;
import com.smartprocurement.entity.JourneyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JourneyMonitoringRepository extends JpaRepository<JourneyMonitoring, Long> {

    Optional<JourneyMonitoring> findByBookingBookingId(Long bookingId);

    List<JourneyMonitoring> findByCentreCentreId(Long centreId);

    List<JourneyMonitoring> findByCentreCentreIdAndJourneyStatus(Long centreId, JourneyStatus journeyStatus);

    List<JourneyMonitoring> findByJourneyStatus(JourneyStatus journeyStatus);
}
