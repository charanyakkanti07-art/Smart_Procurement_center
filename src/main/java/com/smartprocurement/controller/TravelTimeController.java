package com.smartprocurement.controller;

import com.smartprocurement.dto.LocationDTO;
import com.smartprocurement.dto.TravelTimeRequestDTO;
import com.smartprocurement.dto.TravelTimeResponseDTO;
import com.smartprocurement.service.TravelTimeEngine;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/travel-time")
@CrossOrigin(origins = "*")
public class TravelTimeController {

    @Autowired
    private TravelTimeEngine travelTimeEngine;

    @PostMapping
    public ResponseEntity<TravelTimeResponseDTO> computeTravelTimePost(
            @Valid @RequestBody TravelTimeRequestDTO request,
            BindingResult bindingResult) {
        
        if (bindingResult.hasErrors()) {
            String errorMsg = bindingResult.getAllErrors().get(0).getDefaultMessage();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(TravelTimeResponseDTO.error(errorMsg));
        }

        TravelTimeResponseDTO response = travelTimeEngine.calculateTravelTime(request.getOrigin(), request.getDestination());
        if (!response.isSuccess()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<TravelTimeResponseDTO> computeTravelTimeGet(
            @RequestParam(required = false) Double originLat,
            @RequestParam(required = false) Double originLon,
            @RequestParam(required = false) Double destLat,
            @RequestParam(required = false) Double destLon) {

        LocationDTO origin = new LocationDTO(originLat, originLon);
        LocationDTO destination = new LocationDTO(destLat, destLon);

        TravelTimeResponseDTO response = travelTimeEngine.calculateTravelTime(origin, destination);
        if (!response.isSuccess()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        return ResponseEntity.ok(response);
    }
}
