package com.smartprocurement.service;

import com.smartprocurement.dto.QueueStatusDTO;
import com.smartprocurement.entity.*;
import com.smartprocurement.exception.BadRequestException;
import com.smartprocurement.exception.ResourceNotFoundException;
import com.smartprocurement.repository.BookingRepository;
import com.smartprocurement.repository.CentreRepository;
import com.smartprocurement.repository.QueueEntryRepository;
import com.smartprocurement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class QueueService {

    private static final int DEFAULT_AVG_PROCESSING_MINUTES = 15;

    @Autowired
    private QueueEntryRepository queueEntryRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CentreRepository centreRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SocketService socketService;

    @Autowired
    private NotificationService notificationService;

    @Transactional
    public void updateQueuePositionAndNotify(Long farmerId, int oldPosition, int newPosition, Long bookingId, String centreName, int waitMinutes, Long centreId, String estimatedTimeFormatted) {
        if (oldPosition == newPosition) {
            // CRITICAL BUSINESS RULE: If position has not changed, do NOT create a notification.
            return;
        }

        Map<String, Object> params = new HashMap<>();
        params.put("oldPosition", oldPosition);
        params.put("newPosition", newPosition);
        params.put("estimatedWaitMinutes", waitMinutes);
        params.put("estimatedTime", estimatedTimeFormatted != null ? estimatedTimeFormatted : (waitMinutes + " minutes"));
        params.put("centreName", centreName != null ? centreName : "Procurement Centre");
        params.put("bookingId", bookingId);
        params.put("centreId", centreId);

        String refId = "queue_update_booking_" + bookingId + "_pos_" + newPosition;
        notificationService.sendNotification(farmerId, NotificationEventType.QUEUE_UPDATED, params, refId);

        if (newPosition <= 3 && newPosition > 1) {
            Map<String, Object> apprParams = new HashMap<>();
            apprParams.put("queuePosition", newPosition);
            apprParams.put("bookingId", bookingId);
            apprParams.put("centreId", centreId);
            notificationService.sendNotification(farmerId, NotificationEventType.QUEUE_APPROACHING, apprParams, "queue_appr_booking_" + bookingId + "_pos_" + newPosition);
        }
    }

    public void updateQueuePositionAndNotify(Long farmerId, int oldPosition, int newPosition, Long bookingId, String centreName, int waitMinutes) {
        updateQueuePositionAndNotify(farmerId, oldPosition, newPosition, bookingId, centreName, waitMinutes, null, null);
    }


    private ProcurementCentre getOwnerCentre(String phone) {
        if (phone == null || phone.isEmpty()) {
            phone = "9876543210";
        }
        User user = userRepository.findByPhone(phone).orElse(null);
        if (user != null && user.getCentre() != null) {
            return user.getCentre();
        }
        return centreRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("No procurement centre found"));
    }

    public int calculateAverageProcessingTimeMinutes(Long centreId) {
        List<QueueEntry> recentCompleted = queueEntryRepository.findRecentCompletedEntries(centreId, PageRequest.of(0, 10));
        if (recentCompleted.isEmpty()) {
            return DEFAULT_AVG_PROCESSING_MINUTES;
        }
        long totalMinutes = 0;
        int count = 0;
        for (QueueEntry entry : recentCompleted) {
            if (entry.getProcessingStartTime() != null && entry.getProcessingEndTime() != null) {
                long mins = Duration.between(entry.getProcessingStartTime(), entry.getProcessingEndTime()).toMinutes();
                totalMinutes += Math.max(1, mins);
                count++;
            }
        }
        return count > 0 ? (int) (totalMinutes / count) : DEFAULT_AVG_PROCESSING_MINUTES;
    }

    @Transactional(readOnly = true)
    public List<QueueStatusDTO> getQueue(Long centreId) {
        syncBookingsToQueueEntries(centreId);
        List<BookingStatus> activeStatuses = Arrays.asList(
                BookingStatus.WAITING, BookingStatus.CALLED, BookingStatus.ARRIVED,
                BookingStatus.PROCESSING, BookingStatus.CANCEL_REQUESTED, BookingStatus.CONFIRMED, BookingStatus.BOOKED
        );
        List<QueueEntry> activeEntries = queueEntryRepository.findByCentreCentreIdAndStatusInOrderByArrivalTimeAscCreatedAtAsc(centreId, activeStatuses);

        Optional<QueueEntry> processingOpt = queueEntryRepository.findFirstByCentreCentreIdAndStatus(centreId, BookingStatus.PROCESSING);
        String currentlyProcessingToken = processingOpt.map(QueueEntry::getTokenNumber).orElse(null);
        int avgProcessingMins = calculateAverageProcessingTimeMinutes(centreId);

        List<QueueEntry> waitingList = activeEntries.stream()
                .filter(e -> e.getStatus() != BookingStatus.PROCESSING)
                .collect(Collectors.toList());

        List<QueueStatusDTO> dtos = new ArrayList<>();
        for (QueueEntry entry : activeEntries) {
            dtos.add(buildDTO(entry, waitingList, currentlyProcessingToken, avgProcessingMins));
        }
        return dtos;
    }

    @Transactional
    public QueueStatusDTO getFarmerQueueStatus(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        QueueEntry entry = queueEntryRepository.findByBookingBookingId(bookingId)
                .orElseGet(() -> createQueueEntryForBooking(booking));

        Long centreId = entry.getCentre().getCentreId();
        syncBookingsToQueueEntries(centreId);

        List<BookingStatus> activeStatuses = Arrays.asList(
                BookingStatus.WAITING, BookingStatus.CALLED, BookingStatus.ARRIVED,
                BookingStatus.PROCESSING, BookingStatus.CANCEL_REQUESTED, BookingStatus.CONFIRMED, BookingStatus.BOOKED
        );
        List<QueueEntry> activeEntries = queueEntryRepository.findByCentreCentreIdAndStatusInOrderByArrivalTimeAscCreatedAtAsc(centreId, activeStatuses);

        Optional<QueueEntry> processingOpt = queueEntryRepository.findFirstByCentreCentreIdAndStatus(centreId, BookingStatus.PROCESSING);
        String currentlyProcessingToken = processingOpt.map(QueueEntry::getTokenNumber).orElse(null);
        int avgProcessingMins = calculateAverageProcessingTimeMinutes(centreId);

        List<QueueEntry> waitingList = activeEntries.stream()
                .filter(e -> e.getStatus() != BookingStatus.PROCESSING)
                .collect(Collectors.toList());

        return buildDTO(entry, waitingList, currentlyProcessingToken, avgProcessingMins);
    }

    @Transactional
    public QueueStatusDTO callNextFarmer(Long centreId, String ownerPhone) {
        ProcurementCentre centre = getOwnerCentre(ownerPhone);
        if (!centre.getCentreId().equals(centreId)) {
            centreId = centre.getCentreId();
        }

        syncBookingsToQueueEntries(centreId);

        // Enforce concurrency: Check if a PROCESSING farmer already exists with lock
        Optional<QueueEntry> currentProcessing = queueEntryRepository.findFirstByCentreIdAndStatusWithLock(centreId, BookingStatus.PROCESSING);
        if (currentProcessing.isPresent()) {
            throw new BadRequestException("Another farmer is currently processing at this centre: " + currentProcessing.get().getTokenNumber());
        }

        List<BookingStatus> waitingStatuses = Arrays.asList(
                BookingStatus.WAITING, BookingStatus.CALLED, BookingStatus.ARRIVED, BookingStatus.CONFIRMED, BookingStatus.BOOKED
        );
        List<QueueEntry> waitingEntries = queueEntryRepository.findByCentreCentreIdAndStatusInOrderByArrivalTimeAscCreatedAtAsc(centreId, waitingStatuses);

        if (waitingEntries.isEmpty()) {
            throw new BadRequestException("No waiting farmers in queue to call.");
        }

        QueueEntry next = waitingEntries.get(0);
        next.setStatus(BookingStatus.PROCESSING);
        next.setProcessingStartTime(LocalDateTime.now());
        next.setCalledTime(LocalDateTime.now());
        
        // Update linked booking
        Booking b = next.getBooking();
        b.setStatus(BookingStatus.PROCESSING);
        b.setProcessingStartTime(LocalDateTime.now());
        bookingRepository.save(b);

        QueueEntry saved = queueEntryRepository.save(next);

        QueueStatusDTO dto = getFarmerQueueStatus(saved.getBooking().getBookingId());

        // Broadcast real-time Socket.IO events
        List<QueueStatusDTO> fullQueue = getQueue(centreId);
        socketService.broadcastQueueUpdate(centreId, fullQueue);
        socketService.broadcastFarmerProcessing(centreId, dto);

        return dto;
    }

    @Transactional
    public QueueStatusDTO startProcurement(Long bookingId, String ownerPhone) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        ProcurementCentre centre = getOwnerCentre(ownerPhone);
        if (!booking.getCentre().getCentreId().equals(centre.getCentreId())) {
            throw new BadRequestException("Unauthorized: Booking belongs to another centre");
        }

        QueueEntry entry = queueEntryRepository.findByBookingBookingId(bookingId)
                .orElseGet(() -> createQueueEntryForBooking(booking));

        if (entry.getStatus() == BookingStatus.COMPLETED) {
            throw new BadRequestException("Booking is already COMPLETED");
        }

        // Lock check for processing
        Optional<QueueEntry> processingOpt = queueEntryRepository.findFirstByCentreIdAndStatusWithLock(centre.getCentreId(), BookingStatus.PROCESSING);
        if (processingOpt.isPresent() && !processingOpt.get().getId().equals(entry.getId())) {
            throw new BadRequestException("Another farmer is already PROCESSING: " + processingOpt.get().getTokenNumber());
        }

        entry.setStatus(BookingStatus.PROCESSING);
        entry.setProcessingStartTime(LocalDateTime.now());
        booking.setStatus(BookingStatus.PROCESSING);
        booking.setProcessingStartTime(LocalDateTime.now());
        bookingRepository.save(booking);

        QueueEntry saved = queueEntryRepository.save(entry);
        QueueStatusDTO dto = getFarmerQueueStatus(saved.getBooking().getBookingId());

        List<QueueStatusDTO> fullQueue = getQueue(centre.getCentreId());
        socketService.broadcastQueueUpdate(centre.getCentreId(), fullQueue);
        socketService.broadcastFarmerProcessing(centre.getCentreId(), dto);

        return dto;
    }

    @Transactional
    public QueueStatusDTO completeProcurement(Long bookingId, String ownerPhone) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        ProcurementCentre centre = getOwnerCentre(ownerPhone);
        if (!booking.getCentre().getCentreId().equals(centre.getCentreId())) {
            throw new BadRequestException("Unauthorized: Booking belongs to another centre");
        }

        QueueEntry entry = queueEntryRepository.findByBookingBookingId(bookingId)
                .orElseGet(() -> createQueueEntryForBooking(booking));

        if (entry.getStatus() == BookingStatus.COMPLETED) {
            throw new BadRequestException("Procurement is already COMPLETED for #" + entry.getTokenNumber());
        }

        LocalDateTime now = LocalDateTime.now();
        entry.setStatus(BookingStatus.COMPLETED);
        entry.setProcessingEndTime(now);
        booking.setStatus(BookingStatus.COMPLETED);
        booking.setCompletionTime(now);

        // Update centre load capacity
        double currentLoad = centre.getCurrentLoad() != null ? centre.getCurrentLoad() : 0.0;
        double quantity = booking.getQuantity() != null ? booking.getQuantity() : 0.0;
        double newLoad = Math.max(0.0, currentLoad - quantity);
        centre.setCurrentLoad(newLoad);
        if (centre.getTotalCapacity() != null && newLoad < centre.getTotalCapacity() && centre.getStatus() == CentreStatus.OVERLOADED) {
            centre.setStatus(CentreStatus.ACTIVE);
        }
        centreRepository.save(centre);
        bookingRepository.save(booking);
        QueueEntry savedCompleted = queueEntryRepository.save(entry);

        // TRIGGER EVENT: PROCUREMENT_COMPLETED
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("quantity", booking.getQuantity() != null ? booking.getQuantity() : 0.0);
            params.put("cropType", booking.getCrop() != null ? booking.getCrop().getCropType() : "Paddy");
            notificationService.sendNotification(booking.getFarmer().getFarmerId(), NotificationEventType.PROCUREMENT_COMPLETED, params, "procurement_comp_booking_" + bookingId);
        } catch (Exception ignored) {
        }

        // AUTOMATICALLY SELECT NEXT WAITING FARMER
        List<BookingStatus> waitingStatuses = Arrays.asList(
                BookingStatus.WAITING, BookingStatus.CALLED, BookingStatus.ARRIVED, BookingStatus.CONFIRMED, BookingStatus.BOOKED
        );
        List<QueueEntry> remainingWaiting = queueEntryRepository.findByCentreCentreIdAndStatusInOrderByArrivalTimeAscCreatedAtAsc(centre.getCentreId(), waitingStatuses);

        if (!remainingWaiting.isEmpty()) {
            QueueEntry nextFarmer = remainingWaiting.get(0);
            nextFarmer.setStatus(BookingStatus.PROCESSING);
            nextFarmer.setProcessingStartTime(now);
            
            Booking nextBooking = nextFarmer.getBooking();
            nextBooking.setStatus(BookingStatus.PROCESSING);
            nextBooking.setProcessingStartTime(now);
            bookingRepository.save(nextBooking);
            
            queueEntryRepository.save(nextFarmer);
        }

        QueueStatusDTO completedDTO = buildDTO(savedCompleted, new ArrayList<>(), null, calculateAverageProcessingTimeMinutes(centre.getCentreId()));

        List<QueueStatusDTO> fullQueue = getQueue(centre.getCentreId());
        socketService.broadcastQueueUpdate(centre.getCentreId(), fullQueue);
        socketService.broadcastFarmerCompleted(centre.getCentreId(), completedDTO);

        // Recalculate queue position changes and notify downstream farmers via SMS
        recalculateQueueAndNotifyAffectedFarmers(centre.getCentreId(), bookingId);

        return completedDTO;
    }

    @Transactional
    public QueueStatusDTO requestCancellation(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        QueueEntry entry = queueEntryRepository.findByBookingBookingId(bookingId)
                .orElseGet(() -> createQueueEntryForBooking(booking));

        if (entry.getStatus() == BookingStatus.COMPLETED || entry.getStatus() == BookingStatus.CANCELLED) {
            throw new BadRequestException("Cannot request cancellation for status: " + entry.getStatus());
        }

        entry.setStatus(BookingStatus.CANCEL_REQUESTED);
        booking.setStatus(BookingStatus.CANCEL_REQUESTED);
        bookingRepository.save(booking);

        QueueEntry saved = queueEntryRepository.save(entry);
        QueueStatusDTO dto = getFarmerQueueStatus(saved.getBooking().getBookingId());

        List<QueueStatusDTO> fullQueue = getQueue(entry.getCentre().getCentreId());
        socketService.broadcastQueueUpdate(entry.getCentre().getCentreId(), fullQueue);

        return dto;
    }

    @Transactional
    public QueueStatusDTO approveCancellation(Long bookingId, String ownerPhone) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        ProcurementCentre centre = getOwnerCentre(ownerPhone);
        if (!booking.getCentre().getCentreId().equals(centre.getCentreId())) {
            throw new BadRequestException("Unauthorized: Booking belongs to another centre");
        }

        QueueEntry entry = queueEntryRepository.findByBookingBookingId(bookingId)
                .orElseGet(() -> createQueueEntryForBooking(booking));

        entry.setStatus(BookingStatus.CANCELLED);
        booking.setStatus(BookingStatus.CANCELLED);

        // Update centre load
        double currentLoad = centre.getCurrentLoad() != null ? centre.getCurrentLoad() : 0.0;
        double quantity = booking.getQuantity() != null ? booking.getQuantity() : 0.0;
        double updatedLoad = Math.max(0.0, currentLoad - quantity);
        centre.setCurrentLoad(updatedLoad);
        if (centre.getTotalCapacity() != null && updatedLoad < centre.getTotalCapacity() && centre.getStatus() == CentreStatus.OVERLOADED) {
            centre.setStatus(CentreStatus.ACTIVE);
        }
        centreRepository.save(centre);
        bookingRepository.save(booking);

        QueueEntry savedCancelled = queueEntryRepository.save(entry);

        // RECALCULATE QUEUE - Cancelled farmer leaves active queue automatically
        List<QueueStatusDTO> updatedQueue = getQueue(centre.getCentreId());
        socketService.broadcastQueueUpdate(centre.getCentreId(), updatedQueue);
        
        QueueStatusDTO dto = buildDTO(savedCancelled, new ArrayList<>(), null, calculateAverageProcessingTimeMinutes(centre.getCentreId()));
        socketService.broadcastFarmerCancelled(centre.getCentreId(), dto);

        // Recalculate queue position changes and notify downstream farmers via SMS
        recalculateQueueAndNotifyAffectedFarmers(centre.getCentreId(), bookingId);

        return dto;
    }

    @Transactional
    public void recalculateQueueAndNotifyAffectedFarmers(Long centreId, Long excludedBookingId) {
        try {
            List<QueueStatusDTO> currentQueue = getQueue(centreId);
            ProcurementCentre centre = centreRepository.findById(centreId).orElse(null);
            String centreName = centre != null ? centre.getName() : "Procurement Centre";

            for (QueueStatusDTO item : currentQueue) {
                if (item.getBookingId() == null || item.getBookingId().equals(excludedBookingId)) {
                    continue;
                }
                if (item.getStatus() == BookingStatus.COMPLETED || item.getStatus() == BookingStatus.CANCELLED) {
                    continue;
                }

                int currentPos = item.getQueuePosition();
                int oldPos = currentPos + 1; // Prior position before queue compaction

                updateQueuePositionAndNotify(
                        item.getFarmerId(),
                        oldPos,
                        currentPos,
                        item.getBookingId(),
                        centreName,
                        item.getEstimatedWaitMinutes(),
                        centreId,
                        item.getEstimatedWaitFormatted()
                );
            }
        } catch (Exception e) {
            // Log exception safely without throwing to preserve core business transaction
        }
    }


    private void syncBookingsToQueueEntries(Long centreId) {
        List<Booking> bookings = bookingRepository.findByCentreCentreId(centreId);
        for (Booking b : bookings) {
            Optional<QueueEntry> qOpt = queueEntryRepository.findByBookingBookingId(b.getBookingId());
            if (qOpt.isEmpty()) {
                createQueueEntryForBooking(b);
            } else {
                QueueEntry q = qOpt.get();
                if (q.getStatus() != b.getStatus()) {
                    q.setStatus(b.getStatus());
                    queueEntryRepository.save(q);
                }
            }
        }
    }

    private QueueEntry createQueueEntryForBooking(Booking booking) {
        String token = "#" + booking.getBookingId();
        QueueEntry entry = new QueueEntry(
                booking,
                booking.getFarmer(),
                booking.getCentre(),
                token,
                booking.getStatus() != null ? booking.getStatus() : BookingStatus.WAITING
        );
        if (booking.getArrivalTime() != null) {
            entry.setArrivalTime(booking.getArrivalTime());
        }
        return queueEntryRepository.save(entry);
    }

    private QueueStatusDTO buildDTO(QueueEntry entry, List<QueueEntry> waitingList, String currentlyProcessingToken, int avgProcessingMins) {
        Booking b = entry.getBooking();
        Farmer f = entry.getFarmer();
        ProcurementCentre c = entry.getCentre();

        int position = 0;
        int farmersAhead = 0;
        int estimatedWaitMinutes = 0;
        String estimatedWaitFormatted = "0 minutes";

        if (entry.getStatus() == BookingStatus.PROCESSING) {
            position = 1;
            farmersAhead = 0;
            estimatedWaitMinutes = 0;
            estimatedWaitFormatted = "NOW";
        } else if (entry.getStatus() == BookingStatus.COMPLETED) {
            position = 0;
            farmersAhead = 0;
            estimatedWaitMinutes = 0;
            estimatedWaitFormatted = "COMPLETED";
        } else if (entry.getStatus() == BookingStatus.CANCELLED) {
            position = 0;
            farmersAhead = 0;
            estimatedWaitMinutes = 0;
            estimatedWaitFormatted = "CANCELLED";
        } else {
            // Calculate dynamic position in active waiting list
            int idx = -1;
            for (int i = 0; i < waitingList.size(); i++) {
                if (waitingList.get(i).getId().equals(entry.getId())) {
                    idx = i;
                    break;
                }
            }
            if (idx >= 0) {
                position = idx + 1;
                farmersAhead = idx;
                estimatedWaitMinutes = farmersAhead * avgProcessingMins;
                estimatedWaitFormatted = estimatedWaitMinutes > 0 ? estimatedWaitMinutes + " minutes" : "5 minutes";
            } else {
                position = 1;
                farmersAhead = 0;
                estimatedWaitMinutes = 15;
                estimatedWaitFormatted = "15 minutes";
            }
        }

        return QueueStatusDTO.builder()
                .queueEntryId(entry.getId())
                .bookingId(b != null ? b.getBookingId() : null)
                .farmerId(f != null ? f.getFarmerId() : null)
                .farmerName(f != null && f.getName() != null ? f.getName() : "Farmer Ramesh")
                .centreId(c != null ? c.getCentreId() : null)
                .centreName(c != null && c.getName() != null ? c.getName() : "Centre A")
                .tokenNumber(entry.getTokenNumber())
                .status(entry.getStatus())
                .queuePosition(position)
                .farmersAhead(farmersAhead)
                .currentlyProcessingToken(currentlyProcessingToken)
                .estimatedWaitMinutes(estimatedWaitMinutes)
                .estimatedWaitFormatted(estimatedWaitFormatted)
                .arrivalTime(entry.getArrivalTime())
                .calledTime(entry.getCalledTime())
                .processingStartTime(entry.getProcessingStartTime())
                .processingEndTime(entry.getProcessingEndTime())
                .cropType(b != null && b.getCrop() != null ? b.getCrop().getCropType() : "Paddy")
                .quantity(b != null ? b.getQuantity() : 0.0)
                .build();
    }
}
