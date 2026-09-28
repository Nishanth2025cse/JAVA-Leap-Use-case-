package com.example.wastewater.service;

import com.example.wastewater.entity.EnvironmentalSettings;
import com.example.wastewater.repository.EnvironmentalSettingsRepository;
import org.springframework.stereotype.Service;

@Service
public class SettingsService {

    private final EnvironmentalSettingsRepository settingsRepository;

    public SettingsService(EnvironmentalSettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
    }

    public EnvironmentalSettings getSettings() {
        return settingsRepository.findAll().stream().findFirst()
                .orElseGet(() -> {
                    EnvironmentalSettings defaults = new EnvironmentalSettings();
                    defaults.setDefaultCodLimit(250.0);
                    defaults.setFineRate(50.0);
                    defaults.setCurrency("$");
                    return settingsRepository.save(defaults);
                });
    }

    public EnvironmentalSettings updateSettings(EnvironmentalSettings updated) {
        EnvironmentalSettings existing = getSettings();
        existing.setDefaultCodLimit(updated.getDefaultCodLimit());
        existing.setFineRate(updated.getFineRate());
        if (updated.getCurrency() != null && !updated.getCurrency().isEmpty()) {
            existing.setCurrency(updated.getCurrency());
        }
        return settingsRepository.save(existing);
    }
}
