package com.example.wastewater.controller;

import com.example.wastewater.entity.TelemetryReading;
import com.example.wastewater.service.TelemetryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wastewater/telemetry")
@CrossOrigin(origins = "*")
public class TelemetryController {

    private final TelemetryService telemetryService;

    public TelemetryController(TelemetryService telemetryService) {
        this.telemetryService = telemetryService;
    }

    @GetMapping
    public ResponseEntity<List<TelemetryReading>> getAllTelemetry() {
        return ResponseEntity.ok(telemetryService.getAllTelemetry());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TelemetryReading> getTelemetryById(@PathVariable Long id) {
        return ResponseEntity.ok(telemetryService.getTelemetryById(id));
    }

    @PostMapping
    public ResponseEntity<TelemetryReading> submitTelemetry(@Valid @RequestBody TelemetryReading reading) {
        TelemetryReading created = telemetryService.submitTelemetry(reading);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
}
