package com.example.wastewater.service;

import com.example.wastewater.entity.IndustrialFactory;
import com.example.wastewater.enums.FactoryStatus;
import com.example.wastewater.exception.BadRequestException;
import com.example.wastewater.exception.ResourceNotFoundException;
import com.example.wastewater.repository.IndustrialFactoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FactoryService {

    private final IndustrialFactoryRepository factoryRepository;

    public FactoryService(IndustrialFactoryRepository factoryRepository) {
        this.factoryRepository = factoryRepository;
    }

    public List<IndustrialFactory> getAllFactories() {
        return factoryRepository.findAll();
    }

    public IndustrialFactory getFactoryById(Long id) {
        return factoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Industrial factory not found with id: " + id));
    }

    public IndustrialFactory createFactory(IndustrialFactory factory) {
        if (factoryRepository.findByRegistrationNumber(factory.getRegistrationNumber()).isPresent()) {
            throw new BadRequestException("Factory registration number already exists: " + factory.getRegistrationNumber());
        }
        if (factory.getStatus() == null) {
            factory.setStatus(FactoryStatus.ACTIVE);
        }
        return factoryRepository.save(factory);
    }

    public IndustrialFactory updateFactory(Long id, IndustrialFactory details) {
        IndustrialFactory existing = getFactoryById(id);
        existing.setFactoryName(details.getFactoryName());
        existing.setContactPerson(details.getContactPerson());
        existing.setPhone(details.getPhone());
        existing.setEmail(details.getEmail());
        existing.setAddress(details.getAddress());
        existing.setCity(details.getCity());
        existing.setIndustryType(details.getIndustryType());
        existing.setPermittedCodLimit(details.getPermittedCodLimit());
        if (details.getStatus() != null) {
            existing.setStatus(details.getStatus());
        }
        return factoryRepository.save(existing);
    }

    public void deleteFactory(Long id) {
        IndustrialFactory factory = getFactoryById(id);
        factoryRepository.delete(factory);
    }

    public List<IndustrialFactory> searchFactories(String query) {
        if (query == null || query.trim().isEmpty()) {
            return factoryRepository.findAll();
        }
        return factoryRepository.searchFactories(query.trim());
    }
}
