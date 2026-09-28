package com.example.wastewater.controller;

import com.example.wastewater.entity.EnvironmentalSettings;
import com.example.wastewater.service.SettingsService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wastewater/settings")
@CrossOrigin(origins = "*")
public class SettingsController {

    private final SettingsService settingsService;

    public SettingsController(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    @GetMapping
    public ResponseEntity<EnvironmentalSettings> getSettings() {
        return ResponseEntity.ok(settingsService.getSettings());
    }

    @PutMapping
    public ResponseEntity<EnvironmentalSettings> updateSettings(@Valid @RequestBody EnvironmentalSettings settings) {
        return ResponseEntity.ok(settingsService.updateSettings(settings));
    }
}
