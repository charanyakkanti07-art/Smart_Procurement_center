package com.smartprocurement.controller;

import com.smartprocurement.dto.ProcurementDTO;
import com.smartprocurement.dto.ProcurementRequestDTO;
import com.smartprocurement.service.ProcurementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/procurements")
@CrossOrigin(origins = "*")
public class ProcurementController {

    @Autowired
    private ProcurementService procurementService;

    @PostMapping("/arrival/{bookingId}")
    public ResponseEntity<ProcurementDTO> markArrival(@PathVariable Long bookingId) {
        ProcurementDTO dto = procurementService.markArrival(bookingId);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/verify/{bookingId}")
    public ResponseEntity<ProcurementDTO> verifyFarmer(@PathVariable Long bookingId, @RequestBody(required = false) ProcurementRequestDTO request) {
        String operator = request != null ? request.getOperatorName() : "Operator";
        ProcurementDTO dto = procurementService.verifyFarmer(bookingId, operator);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/weighing/{bookingId}")
    public ResponseEntity<ProcurementDTO> recordWeighing(@PathVariable Long bookingId, @RequestBody ProcurementRequestDTO request) {
        ProcurementDTO dto = procurementService.recordWeighing(bookingId, request);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/quality-check/{bookingId}")
    public ResponseEntity<ProcurementDTO> recordQualityCheck(@PathVariable Long bookingId, @RequestBody ProcurementRequestDTO request) {
        ProcurementDTO dto = procurementService.recordQualityCheck(bookingId, request);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/confirm/{bookingId}")
    public ResponseEntity<ProcurementDTO> confirmProcurement(@PathVariable Long bookingId, @RequestBody(required = false) ProcurementRequestDTO request) {
        String operator = request != null ? request.getOperatorName() : "Operator";
        ProcurementDTO dto = procurementService.confirmProcurement(bookingId, operator);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<ProcurementDTO> getProcurementByBookingId(@PathVariable Long bookingId) {
        ProcurementDTO dto = procurementService.getProcurementByBookingId(bookingId);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/farmer/{farmerId}")
    public ResponseEntity<List<ProcurementDTO>> getProcurementsByFarmerId(@PathVariable Long farmerId) {
        List<ProcurementDTO> dtos = procurementService.getProcurementsByFarmerId(farmerId);
        return ResponseEntity.ok(dtos);
    }
}
