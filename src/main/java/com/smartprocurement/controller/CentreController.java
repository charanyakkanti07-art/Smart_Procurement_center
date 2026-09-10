package com.smartprocurement.controller;

import com.smartprocurement.dto.CapacityUpdateRequest;
import com.smartprocurement.dto.CentreDTO;
import com.smartprocurement.dto.CentreRecommendationResponseDTO;
import com.smartprocurement.entity.ProcurementCentre;
import com.smartprocurement.service.CentreRecommendationService;
import com.smartprocurement.service.CentreService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/centres")
@CrossOrigin(origins = "*")
public class CentreController {

    @Autowired
    private CentreService centreService;

    @Autowired
    private CentreRecommendationService recommendationService;

    @PostMapping
    public ResponseEntity<CentreDTO> createCentre(@RequestBody ProcurementCentre centre) {
        CentreDTO dto = centreService.createCentre(centre);
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CentreDTO>> getAllCentres() {
        List<CentreDTO> centres = centreService.getAllCentres();
        return ResponseEntity.ok(centres);
    }

    @GetMapping("/recommend")
    public ResponseEntity<CentreRecommendationResponseDTO> getRecommendations(
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude,
            @RequestParam(required = false) String cropType,
            @RequestParam(required = false) Double quantity) {
        CentreRecommendationResponseDTO response = recommendationService.getRecommendations(latitude, longitude, cropType, quantity);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CentreDTO> getCentreById(@PathVariable Long id) {
        CentreDTO dto = centreService.getCentreById(id);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}/capacity")
    public ResponseEntity<CentreDTO> updateCapacity(@PathVariable Long id, @Valid @RequestBody CapacityUpdateRequest request) {
        CentreDTO dto = centreService.updateCapacity(id, request);
        return ResponseEntity.ok(dto);
    }
}
