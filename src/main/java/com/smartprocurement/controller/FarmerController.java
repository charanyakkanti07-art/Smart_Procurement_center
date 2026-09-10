package com.smartprocurement.controller;

import com.smartprocurement.dto.*;
import com.smartprocurement.service.BookingService;
import com.smartprocurement.service.FarmerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/farmers")
@CrossOrigin(origins = "*")
public class FarmerController {

    @Autowired
    private FarmerService farmerService;

    @Autowired
    private BookingService bookingService;

    @PostMapping
    public ResponseEntity<FarmerDTO> createFarmer(@Valid @RequestBody RegisterRequest request) {
        FarmerDTO dto = farmerService.createFarmer(request);
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FarmerDTO> getFarmerById(@PathVariable Long id) {
        FarmerDTO dto = farmerService.getFarmerById(id);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FarmerDTO> updateFarmer(@PathVariable Long id, @RequestBody RegisterRequest request) {
        FarmerDTO dto = farmerService.updateFarmer(id, request);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}/language")
    public ResponseEntity<FarmerDTO> updateLanguage(@PathVariable Long id, @RequestBody java.util.Map<String, String> body) {
        String language = body != null ? body.get("language") : null;
        if (language == null && body != null) language = body.get("preferredLanguage");
        FarmerDTO dto = farmerService.updateLanguage(id, language);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/{id}/crops")
    public ResponseEntity<CropDTO> addCrop(@PathVariable("id") Long farmerId, @Valid @RequestBody CropRequest request) {
        CropDTO dto = farmerService.addCrop(farmerId, request);
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    @GetMapping("/{id}/crops")
    public ResponseEntity<List<CropDTO>> getCropsByFarmer(@PathVariable("id") Long farmerId) {
        List<CropDTO> crops = farmerService.getCropsByFarmer(farmerId);
        return ResponseEntity.ok(crops);
    }

    @GetMapping("/{id}/bookings")
    public ResponseEntity<List<BookingResponse>> getBookingsByFarmer(@PathVariable("id") Long farmerId) {
        List<BookingResponse> bookings = bookingService.getBookingsByFarmer(farmerId);
        return ResponseEntity.ok(bookings);
    }
}
