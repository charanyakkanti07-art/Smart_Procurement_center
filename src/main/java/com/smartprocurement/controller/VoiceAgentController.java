package com.smartprocurement.controller;

import com.smartprocurement.dto.VoiceEventPayloadDTO;
import com.smartprocurement.entity.JourneyMonitoring;
import com.smartprocurement.service.JourneyMonitoringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/voice-agent")
@CrossOrigin(origins = "*")
public class VoiceAgentController {

    @Autowired
    private JourneyMonitoringService journeyService;

    @PostMapping("/events")
    public ResponseEntity<Map<String, Object>> handleVoiceEvent(@RequestBody VoiceEventPayloadDTO payload) {
        JourneyMonitoring journey = journeyService.processVoiceEvent(payload);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Voice event processed successfully: " + payload.getEvent());
        response.put("journeyStatus", journey.getJourneyStatus());
        response.put("expectedArrivalTime", journey.getExpectedArrivalTime());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/simulate")
    public ResponseEntity<Map<String, Object>> simulateVoiceCall(@RequestBody VoiceEventPayloadDTO payload) {
        payload.setSource("SIMULATOR");
        JourneyMonitoring journey = journeyService.processVoiceEvent(payload);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Simulated AI Voice Call Event: " + payload.getEvent());
        response.put("journeyStatus", journey.getJourneyStatus());
        response.put("expectedArrivalTime", journey.getExpectedArrivalTime());
        return ResponseEntity.ok(response);
    }
}
