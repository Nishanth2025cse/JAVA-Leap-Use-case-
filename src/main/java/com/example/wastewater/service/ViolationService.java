package com.example.wastewater.service;

import com.example.wastewater.entity.EnvironmentalViolation;
import com.example.wastewater.enums.ViolationStatus;
import com.example.wastewater.exception.ResourceNotFoundException;
import com.example.wastewater.repository.EnvironmentalViolationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ViolationService {

    private final EnvironmentalViolationRepository violationRepository;

    public ViolationService(EnvironmentalViolationRepository violationRepository) {
        this.violationRepository = violationRepository;
    }

    public List<EnvironmentalViolation> getAllViolations() {
        return violationRepository.findAll();
    }

    public EnvironmentalViolation getViolationById(Long id) {
        return violationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Environmental violation not found with id: " + id));
    }

    public List<EnvironmentalViolation> getViolationsByFactory(Long factoryId) {
        return violationRepository.findByFactoryId(factoryId);
    }

    public List<EnvironmentalViolation> getOpenViolationsByFactory(Long factoryId) {
        return violationRepository.findByFactoryIdAndStatus(factoryId, ViolationStatus.OPEN);
    }

    public EnvironmentalViolation updateViolationStatus(Long id, ViolationStatus status) {
        EnvironmentalViolation violation = getViolationById(id);
        violation.setStatus(status);
        return violationRepository.save(violation);
    }
}
