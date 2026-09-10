package com.smartprocurement.service;

import com.smartprocurement.dto.ActionRequiredDTO;
import com.smartprocurement.dto.ApprovalRequestDTO;
import com.smartprocurement.entity.*;
import com.smartprocurement.exception.BadRequestException;
import com.smartprocurement.exception.ResourceNotFoundException;
import com.smartprocurement.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ApprovalService {

    private static final Logger logger = LoggerFactory.getLogger(ApprovalService.class);

    @Autowired
    private ApprovalRequestRepository approvalRepository;

    @Autowired
    private JourneyMonitoringRepository journeyRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CentreRepository centreRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private AuditService auditService;

    @Autowired
    private QueueService queueService;

    private ProcurementCentre getOwnerCentre(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            phone = "9876543210";
        }
        User user = userRepository.findByPhone(phone).orElse(null);
        if (user != null && user.getCentre() != null) {
            return user.getCentre();
        }
        return centreRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("No procurement centre found for owner"));
    }

    @Transactional(readOnly = true)
    public List<ApprovalRequestDTO> getPendingApprovals(String ownerPhone) {
        ProcurementCentre centre = getOwnerCentre(ownerPhone);
        List<ApprovalRequest> pending = approvalRepository.findByCentreCentreIdAndStatus(centre.getCentreId(), ApprovalStatus.PENDING);
        return pending.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ApprovalRequestDTO getApprovalRequestById(Long id) {
        ApprovalRequest req = approvalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Approval request not found with id: " + id));
        return mapToDTO(req);
    }

    @Transactional(readOnly = true)
    public List<ActionRequiredDTO> getActionRequiredAlerts(String ownerPhone) {
        ProcurementCentre centre = getOwnerCentre(ownerPhone);
        // Sync missed arrivals before returning
        List<JourneyMonitoring> journeys = journeyRepository.findByCentreCentreId(centre.getCentreId());
        List<ActionRequiredDTO> alerts = new ArrayList<>();

        LocalDateTime now = LocalDateTime.now();
        for (JourneyMonitoring j : journeys) {
            Booking b = j.getBooking();
            if (b.getStatus() == BookingStatus.COMPLETED || b.getStatus() == BookingStatus.CANCELLED) {
                continue;
            }
            if (j.getJourneyStatus() == JourneyStatus.MISSED_ARRIVAL) {
                alerts.add(ActionRequiredDTO.builder()
                        .bookingId(b.getBookingId())
                        .farmerId(b.getFarmer().getFarmerId())
                        .farmerName(b.getFarmer().getName())
                        .farmerPhone(b.getFarmer().getPhone())
                        .centreId(centre.getCentreId())
                        .centreName(centre.getName())
                        .slot(b.getSlot())
                        .journeyStatus(j.getJourneyStatus())
                        .issueType(VoiceEventType.MISSED_EXPECTED_ARRIVAL)
                        .issueDescription("Farmer missed expected arrival time (" + (j.getExpectedArrivalTime() != null ? j.getExpectedArrivalTime().toLocalTime() : "N/A") + "). Booking retained in queue.")
                        .expectedArrivalTime(j.getExpectedArrivalTime())
                        .timestamp(j.getUpdatedAt() != null ? j.getUpdatedAt() : now)
                        .build());
            } else if (j.getJourneyStatus() == JourneyStatus.UNREACHABLE && j.getFailedCallCount() >= 2) {
                alerts.add(ActionRequiredDTO.builder()
                        .bookingId(b.getBookingId())
                        .farmerId(b.getFarmer().getFarmerId())
                        .farmerName(b.getFarmer().getName())
                        .farmerPhone(b.getFarmer().getPhone())
                        .centreId(centre.getCentreId())
                        .centreName(centre.getName())
                        .slot(b.getSlot())
                        .journeyStatus(j.getJourneyStatus())
                        .issueType(VoiceEventType.FARMER_NOT_REACHABLE)
                        .issueDescription("Farmer call failed " + j.getFailedCallCount() + " times. AI agent unable to reach farmer.")
                        .expectedArrivalTime(j.getExpectedArrivalTime())
                        .timestamp(j.getUpdatedAt() != null ? j.getUpdatedAt() : now)
                        .build());
            }
        }
        return alerts;
    }

    @Transactional
    public ApprovalRequestDTO approveRequest(Long requestId, String ownerPhone) {
        ProcurementCentre centre = getOwnerCentre(ownerPhone);
        ApprovalRequest request = approvalRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Approval request not found with id: " + requestId));

        if (!request.getCentre().getCentreId().equals(centre.getCentreId())) {
            throw new BadRequestException("Unauthorized: Approval request belongs to another procurement centre");
        }

        if (request.getStatus() != ApprovalStatus.PENDING) {
            throw new BadRequestException("Approval request is already processed with status: " + request.getStatus());
        }

        Booking booking = request.getBooking();
        LocalDateTime now = LocalDateTime.now();

        if (request.getType() == ApprovalType.CANCELLATION) {
            // EXECUTE CANCELLATION
            booking.setStatus(BookingStatus.CANCELLED);
            bookingRepository.save(booking);

            // Update Centre Load
            double currentLoad = centre.getCurrentLoad() != null ? centre.getCurrentLoad() : 0.0;
            double qty = booking.getQuantity() != null ? booking.getQuantity() : 0.0;
            double newLoad = Math.max(0.0, currentLoad - qty);
            centre.setCurrentLoad(newLoad);
            if (centre.getTotalCapacity() != null && newLoad < centre.getTotalCapacity() && centre.getStatus() == CentreStatus.OVERLOADED) {
                centre.setStatus(CentreStatus.ACTIVE);
            }
            centreRepository.save(centre);

            // Recalculate queue
            queueService.getQueue(centre.getCentreId());

            request.setStatus(ApprovalStatus.APPROVED);
            request.setProcessedAt(now);
            approvalRepository.save(request);

            // Update journey if exists
            journeyRepository.findByBookingBookingId(booking.getBookingId()).ifPresent(j -> {
                j.setJourneyStatus(JourneyStatus.COMPLETED);
                journeyRepository.save(j);
            });

            // AUDIT & NOTIFICATION
            auditService.recordAudit("OWNER_APPROVED", booking.getBookingId(), booking.getFarmer().getFarmerId(),
                    centre.getCentreId(), ApprovalActor.OWNER, "Owner APPROVED cancellation request for Booking #" + booking.getBookingId());
            auditService.recordAudit("BOOKING_UPDATED", booking.getBookingId(), booking.getFarmer().getFarmerId(),
                    centre.getCentreId(), ApprovalActor.OWNER, "Booking status updated to CANCELLED");
            auditService.recordAudit("QUEUE_RECALCULATED", booking.getBookingId(), booking.getFarmer().getFarmerId(),
                    centre.getCentreId(), ApprovalActor.SYSTEM, "Queue recalculated following booking cancellation");

            Map<String, Object> params = new HashMap<>();
            params.put("action", "approved");
            params.put("type", "Cancellation");
            notificationService.sendNotification(booking.getFarmer().getFarmerId(), NotificationEventType.OWNER_APPROVAL, params, "appr_approved_cancel_" + booking.getBookingId());

        } else if (request.getType() == ApprovalType.RESCHEDULE) {
            // EXECUTE RESCHEDULING
            if (request.getRequestedDate() != null) {
                booking.setBookingDate(request.getRequestedDate());
            }
            if (request.getRequestedSlot() != null) {
                booking.setSlot(request.getRequestedSlot());
            }
            booking.setStatus(BookingStatus.RESCHEDULED);
            bookingRepository.save(booking);

            // Recalculate queue
            queueService.getQueue(centre.getCentreId());

            request.setStatus(ApprovalStatus.APPROVED);
            request.setProcessedAt(now);
            approvalRepository.save(request);

            // AUDIT & NOTIFICATION
            auditService.recordAudit("OWNER_APPROVED", booking.getBookingId(), booking.getFarmer().getFarmerId(),
                    centre.getCentreId(), ApprovalActor.OWNER, "Owner APPROVED reschedule request to " + booking.getBookingDate() + " (" + booking.getSlot() + ")");
            auditService.recordAudit("BOOKING_UPDATED", booking.getBookingId(), booking.getFarmer().getFarmerId(),
                    centre.getCentreId(), ApprovalActor.OWNER, "Booking rescheduled to " + booking.getBookingDate() + " (" + booking.getSlot() + ")");

            Map<String, Object> params = new HashMap<>();
            params.put("action", "approved");
            params.put("type", "Rescheduling");
            params.put("slot", booking.getSlot());
            params.put("bookingDate", booking.getBookingDate() != null ? booking.getBookingDate().toString() : "");
            notificationService.sendNotification(booking.getFarmer().getFarmerId(), NotificationEventType.OWNER_APPROVAL, params, "appr_approved_resched_" + booking.getBookingId());
        }

        return mapToDTO(request);
    }

    @Transactional
    public ApprovalRequestDTO rejectRequest(Long requestId, String ownerPhone) {
        ProcurementCentre centre = getOwnerCentre(ownerPhone);
        ApprovalRequest request = approvalRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Approval request not found with id: " + requestId));

        if (!request.getCentre().getCentreId().equals(centre.getCentreId())) {
            throw new BadRequestException("Unauthorized: Approval request belongs to another procurement centre");
        }

        if (request.getStatus() != ApprovalStatus.PENDING) {
            throw new BadRequestException("Approval request is already processed with status: " + request.getStatus());
        }

        Booking booking = request.getBooking();
        request.setStatus(ApprovalStatus.REJECTED);
        request.setProcessedAt(LocalDateTime.now());
        approvalRepository.save(request);

        // Booking & Queue remain UNCHANGED
        auditService.recordAudit("OWNER_REJECTED", booking.getBookingId(), booking.getFarmer().getFarmerId(),
                centre.getCentreId(), ApprovalActor.OWNER, "Owner REJECTED " + request.getType() + " request for Booking #" + booking.getBookingId() + ". Booking remains active.");

        Map<String, Object> params = new HashMap<>();
        params.put("action", "rejected");
        params.put("type", request.getType().name());
        notificationService.sendNotification(booking.getFarmer().getFarmerId(), NotificationEventType.OWNER_APPROVAL, params, "appr_rejected_" + requestId);

        return mapToDTO(request);
    }

    @Transactional
    public void resolveActionRequired(Long bookingId, String action, String ownerPhone, Map<String, Object> params) {
        ProcurementCentre centre = getOwnerCentre(ownerPhone);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        if (!booking.getCentre().getCentreId().equals(centre.getCentreId())) {
            throw new BadRequestException("Unauthorized: Booking belongs to another centre");
        }

        String act = action != null ? action.toUpperCase() : "KEEP_BOOKING";
        logger.info("Owner resolving Action Required for Booking #{}: {}", bookingId, act);

        if ("CONTACT_FARMER".equals(act)) {
            auditService.recordAudit("OWNER_CONTACTED_FARMER", bookingId, booking.getFarmer().getFarmerId(),
                    centre.getCentreId(), ApprovalActor.OWNER, "Owner manually contacted farmer " + booking.getFarmer().getName() + " (" + booking.getFarmer().getPhone() + ")");
        } else if ("KEEP_BOOKING".equals(act)) {
            journeyRepository.findByBookingBookingId(bookingId).ifPresent(j -> {
                j.setJourneyStatus(JourneyStatus.SCHEDULED);
                journeyRepository.save(j);
            });
            auditService.recordAudit("OWNER_KEPT_BOOKING", bookingId, booking.getFarmer().getFarmerId(),
                    centre.getCentreId(), ApprovalActor.OWNER, "Owner retained Booking #" + bookingId + " in active queue.");
        } else if ("CANCEL".equals(act)) {
            // Owner cancels manually
            booking.setStatus(BookingStatus.CANCELLED);
            bookingRepository.save(booking);
            queueService.getQueue(centre.getCentreId());
            auditService.recordAudit("OWNER_CANCELLED_BOOKING", bookingId, booking.getFarmer().getFarmerId(),
                    centre.getCentreId(), ApprovalActor.OWNER, "Owner manually cancelled Booking #" + bookingId + " following missed arrival/unreachable alert.");
        } else if ("RESCHEDULE".equals(act)) {
            LocalDate newDate = params != null && params.get("newDate") != null ? LocalDate.parse(params.get("newDate").toString()) : LocalDate.now().plusDays(1);
            String newSlot = params != null && params.get("newSlot") != null ? params.get("newSlot").toString() : "Morning Slot";
            booking.setBookingDate(newDate);
            booking.setSlot(newSlot);
            booking.setStatus(BookingStatus.RESCHEDULED);
            bookingRepository.save(booking);
            auditService.recordAudit("OWNER_RESCHEDULED_BOOKING", bookingId, booking.getFarmer().getFarmerId(),
                    centre.getCentreId(), ApprovalActor.OWNER, "Owner manually rescheduled Booking #" + bookingId + " to " + newDate + " (" + newSlot + ")");
        }
    }

    private ApprovalRequestDTO mapToDTO(ApprovalRequest req) {
        Farmer f = req.getFarmer();
        ProcurementCentre c = req.getCentre();
        Booking b = req.getBooking();

        return ApprovalRequestDTO.builder()
                .id(req.getId())
                .type(req.getType())
                .status(req.getStatus())
                .createdBy(req.getCreatedBy())
                .bookingId(b != null ? b.getBookingId() : null)
                .farmerId(f != null ? f.getFarmerId() : null)
                .farmerName(f != null ? f.getName() : "Farmer Ramesh")
                .farmerPhone(f != null ? f.getPhone() : "9876543210")
                .centreId(c != null ? c.getCentreId() : null)
                .centreName(c != null ? c.getName() : "Centre A")
                .currentSlot(b != null ? b.getSlot() : "10:00 AM")
                .currentDate(b != null ? b.getBookingDate() : LocalDate.now())
                .requestedSlot(req.getRequestedSlot())
                .requestedDate(req.getRequestedDate())
                .reason(req.getReason())
                .aiEvent(req.getAiEvent())
                .createdAt(req.getCreatedAt())
                .processedAt(req.getProcessedAt())
                .build();
    }
}
