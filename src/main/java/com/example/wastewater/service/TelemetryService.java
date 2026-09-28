package com.example.wastewater.service;

import com.example.wastewater.entity.EnvironmentalSettings;
import com.example.wastewater.entity.EnvironmentalViolation;
import com.example.wastewater.entity.IndustrialFactory;
import com.example.wastewater.entity.TelemetryReading;
import com.example.wastewater.enums.ComplianceStatus;
import com.example.wastewater.enums.FactoryStatus;
import com.example.wastewater.enums.ViolationStatus;
import com.example.wastewater.exception.ResourceNotFoundException;
import com.example.wastewater.repository.EnvironmentalViolationRepository;
import com.example.wastewater.repository.IndustrialFactoryRepository;
import com.example.wastewater.repository.TelemetryReadingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TelemetryService {

    private final TelemetryReadingRepository telemetryRepository;
    private final IndustrialFactoryRepository factoryRepository;
    private final EnvironmentalViolationRepository violationRepository;
    private final SettingsService settingsService;

    public TelemetryService(TelemetryReadingRepository telemetryRepository,
                            IndustrialFactoryRepository factoryRepository,
                            EnvironmentalViolationRepository violationRepository,
                            SettingsService settingsService) {
        this.telemetryRepository = telemetryRepository;
        this.factoryRepository = factoryRepository;
        this.violationRepository = violationRepository;
        this.settingsService = settingsService;
    }

    public List<TelemetryReading> getAllTelemetry() {
        return telemetryRepository.findAll();
    }

    public TelemetryReading getTelemetryById(Long id) {
        return telemetryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Telemetry record not found with id: " + id));
    }

    @Transactional
    public TelemetryReading submitTelemetry(TelemetryReading reading) {
        Long factoryId = reading.getFactory().getId();
        IndustrialFactory factory = factoryRepository.findById(factoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Industrial factory not found with id: " + factoryId));

        EnvironmentalSettings settings = settingsService.getSettings();
        double permittedLimit = factory.getPermittedCodLimit() != null ? 
                factory.getPermittedCodLimit() : settings.getDefaultCodLimit();

        reading.setFactory(factory);
        reading.setPermittedCodLimit(permittedLimit);
        if (reading.getMeasurementDateTime() == null) {
            reading.setMeasurementDateTime(LocalDateTime.now());
        }

        if (reading.getCodValue() <= permittedLimit) {
            reading.setStatus(ComplianceStatus.COMPLIANT);
            reading.setViolationAmount(0.0);
            TelemetryReading saved = telemetryRepository.save(reading);
            return saved;
        } else {
            reading.setStatus(ComplianceStatus.NON_COMPLIANT);
            double excessCod = reading.getCodValue() - permittedLimit;
            double fineAmount = excessCod * settings.getFineRate();
            reading.setViolationAmount(fineAmount);

            TelemetryReading savedTelemetry = telemetryRepository.save(reading);

            // Create Violation Record
            EnvironmentalViolation violation = new EnvironmentalViolation();
            violation.setFactory(factory);
            violation.setTelemetry(savedTelemetry);
            violation.setCodValue(reading.getCodValue());
            violation.setPermittedLimit(permittedLimit);
            violation.setExcessCod(excessCod);
            violation.setFineAmount(fineAmount);
            violation.setStatus(ViolationStatus.OPEN);
            violation.setViolationDate(savedTelemetry.getMeasurementDateTime());
            violation.setRemarks("Automatic telemetry violation trigger. Excess COD: " + excessCod + " mg/L");

            violationRepository.save(violation);

            // Flag Factory
            factory.setStatus(FactoryStatus.FLAGGED);
            factoryRepository.save(factory);

            return savedTelemetry;
        }
    }
}
