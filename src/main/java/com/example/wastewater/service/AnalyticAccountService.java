package com.example.wastewater.service;

import com.example.wastewater.entity.AnalyticAccount;
import com.example.wastewater.exception.ResourceNotFoundException;
import com.example.wastewater.repository.AnalyticAccountRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnalyticAccountService {

    private final AnalyticAccountRepository analyticAccountRepository;

    public AnalyticAccountService(AnalyticAccountRepository analyticAccountRepository) {
        this.analyticAccountRepository = analyticAccountRepository;
    }

    public List<AnalyticAccount> getAllAnalyticAccounts() {
        return analyticAccountRepository.findAll();
    }

    public AnalyticAccount getById(Long id) {
        return analyticAccountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Analytic account (Treatment Facility) not found with id: " + id));
    }

    public AnalyticAccount create(AnalyticAccount account) {
        return analyticAccountRepository.save(account);
    }

    public AnalyticAccount update(Long id, AnalyticAccount details) {
        AnalyticAccount existing = getById(id);
        existing.setName(details.getName());
        existing.setDescription(details.getDescription());
        existing.setPlantLocation(details.getPlantLocation());
        existing.setActive(details.isActive());
        return analyticAccountRepository.save(existing);
    }
}
