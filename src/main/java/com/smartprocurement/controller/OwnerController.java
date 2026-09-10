package com.smartprocurement.controller;

import com.smartprocurement.dto.AssignSlotRequest;
import com.smartprocurement.dto.BookingResponse;
import com.smartprocurement.dto.OwnerDashboardDTO;
import com.smartprocurement.dto.SkipRequest;
import com.smartprocurement.service.OwnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/owner")
@CrossOrigin(origins = "*")
public class OwnerController {

    @Autowired
    private OwnerService ownerService;

    private String getAuthenticatedPhone(Authentication authentication, String fallbackPhone) {
        if (authentication != null && authentication.getName() != null && !authentication.getName().equals("anonymousUser")) {
            return authentication.getName();
        }
        return fallbackPhone != null ? fallbackPhone : "9876543210";
    }

    @GetMapping("/dashboard")
    public ResponseEntity<OwnerDashboardDTO> getDashboard(Authentication authentication,
                                                         @RequestParam(value = "phone", required = false) String phone) {
        String userPhone = getAuthenticatedPhone(authentication, phone);
        OwnerDashboardDTO dto = ownerService.getDashboardData(userPhone);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/queue")
    public ResponseEntity<List<BookingResponse>> getQueue(Authentication authentication,
                                                         @RequestParam(value = "phone", required = false) String phone) {
        String userPhone = getAuthenticatedPhone(authentication, phone);
        List<BookingResponse> queue = ownerService.getQueue(userPhone);
        return ResponseEntity.ok(queue);
    }

    @PostMapping("/queue/call-next")
    public ResponseEntity<BookingResponse> callNext(Authentication authentication,
                                                     @RequestParam(value = "phone", required = false) String phone) {
        String userPhone = getAuthenticatedPhone(authentication, phone);
        BookingResponse response = ownerService.callNextFarmer(userPhone);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/queue/{bookingId}/arrived")
    public ResponseEntity<BookingResponse> markArrived(Authentication authentication,
                                                       @PathVariable Long bookingId,
                                                       @RequestParam(value = "phone", required = false) String phone) {
        String userPhone = getAuthenticatedPhone(authentication, phone);
        BookingResponse response = ownerService.markArrived(userPhone, bookingId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/queue/{bookingId}/start")
    public ResponseEntity<BookingResponse> startProcurement(Authentication authentication,
                                                            @PathVariable Long bookingId,
                                                            @RequestParam(value = "phone", required = false) String phone) {
        String userPhone = getAuthenticatedPhone(authentication, phone);
        BookingResponse response = ownerService.startProcurement(userPhone, bookingId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/queue/{bookingId}/complete")
    public ResponseEntity<BookingResponse> completeProcurement(Authentication authentication,
                                                               @PathVariable Long bookingId,
                                                               @RequestParam(value = "phone", required = false) String phone) {
        String userPhone = getAuthenticatedPhone(authentication, phone);
        BookingResponse response = ownerService.completeProcurement(userPhone, bookingId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/queue/{bookingId}/skip")
    public ResponseEntity<BookingResponse> skipFarmer(Authentication authentication,
                                                       @PathVariable Long bookingId,
                                                       @RequestBody(required = false) SkipRequest request,
                                                       @RequestParam(value = "phone", required = false) String phone) {
        String userPhone = getAuthenticatedPhone(authentication, phone);
        String reason = request != null ? request.getReason() : "Skipped by operator";
        BookingResponse response = ownerService.skipFarmer(userPhone, bookingId, reason);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/cancellations")
    public ResponseEntity<List<BookingResponse>> getCancellationRequests(Authentication authentication,
                                                                         @RequestParam(value = "phone", required = false) String phone) {
        String userPhone = getAuthenticatedPhone(authentication, phone);
        List<BookingResponse> list = ownerService.getCancellationRequests(userPhone);
        return ResponseEntity.ok(list);
    }

    @PostMapping("/cancellations/{bookingId}/approve")
    public ResponseEntity<BookingResponse> approveCancellation(Authentication authentication,
                                                                @PathVariable Long bookingId,
                                                                @RequestParam(value = "phone", required = false) String phone) {
        String userPhone = getAuthenticatedPhone(authentication, phone);
        BookingResponse response = ownerService.approveCancellation(userPhone, bookingId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/cancellations/{bookingId}/reject")
    public ResponseEntity<BookingResponse> rejectCancellation(Authentication authentication,
                                                               @PathVariable Long bookingId,
                                                               @RequestParam(value = "phone", required = false) String phone) {
        String userPhone = getAuthenticatedPhone(authentication, phone);
        BookingResponse response = ownerService.rejectCancellation(userPhone, bookingId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/rescheduling")
    public ResponseEntity<List<BookingResponse>> getReschedulingRequests(Authentication authentication,
                                                                        @RequestParam(value = "phone", required = false) String phone) {
        String userPhone = getAuthenticatedPhone(authentication, phone);
        List<BookingResponse> list = ownerService.getReschedulingRequests(userPhone);
        return ResponseEntity.ok(list);
    }

    @PostMapping("/rescheduling/{bookingId}/approve")
    public ResponseEntity<BookingResponse> approveRescheduling(Authentication authentication,
                                                               @PathVariable Long bookingId,
                                                               @RequestBody(required = false) AssignSlotRequest request,
                                                               @RequestParam(value = "phone", required = false) String phone) {
        String userPhone = getAuthenticatedPhone(authentication, phone);
        BookingResponse response = ownerService.approveRescheduling(userPhone, bookingId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/rescheduling/{bookingId}/reject")
    public ResponseEntity<BookingResponse> rejectRescheduling(Authentication authentication,
                                                              @PathVariable Long bookingId,
                                                              @RequestParam(value = "phone", required = false) String phone) {
        String userPhone = getAuthenticatedPhone(authentication, phone);
        BookingResponse response = ownerService.rejectRescheduling(userPhone, bookingId);
        return ResponseEntity.ok(response);
    }
}
