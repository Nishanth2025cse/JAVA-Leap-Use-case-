package com.example.wastewater.controller;

import com.example.wastewater.entity.EnvironmentalViolation;
import com.example.wastewater.enums.ViolationStatus;
import com.example.wastewater.service.ViolationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wastewater/violations")
@CrossOrigin(origins = "*")
public class ViolationController {

    private final ViolationService violationService;

    public ViolationController(ViolationService violationService) {
        this.violationService = violationService;
    }

    @GetMapping
    public ResponseEntity<List<EnvironmentalViolation>> getAllViolations() {
        return ResponseEntity.ok(violationService.getAllViolations());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EnvironmentalViolation> getViolationById(@PathVariable Long id) {
        return ResponseEntity.ok(violationService.getViolationById(id));
    }

    @GetMapping("/factory/{factoryId}")
    public ResponseEntity<List<EnvironmentalViolation>> getViolationsByFactory(@PathVariable Long factoryId, @RequestParam(required = false) Boolean openOnly) {
        if (Boolean.TRUE.equals(openOnly)) {
            return ResponseEntity.ok(violationService.getOpenViolationsByFactory(factoryId));
        }
        return ResponseEntity.ok(violationService.getViolationsByFactory(factoryId));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<EnvironmentalViolation> updateStatus(@PathVariable Long id, @RequestParam ViolationStatus status) {
        return ResponseEntity.ok(violationService.updateViolationStatus(id, status));
    }
}
