package com.example.wastewater.service;

import com.example.wastewater.entity.ChemicalSupplier;
import com.example.wastewater.exception.ResourceNotFoundException;
import com.example.wastewater.repository.ChemicalSupplierRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SupplierService {

    private final ChemicalSupplierRepository supplierRepository;

    public SupplierService(ChemicalSupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    public List<ChemicalSupplier> getAllSuppliers() {
        return supplierRepository.findAll();
    }

    public ChemicalSupplier getSupplierById(Long id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Chemical supplier not found with id: " + id));
    }

    public ChemicalSupplier createSupplier(ChemicalSupplier supplier) {
        return supplierRepository.save(supplier);
    }

    public ChemicalSupplier updateSupplier(Long id, ChemicalSupplier details) {
        ChemicalSupplier existing = getSupplierById(id);
        existing.setSupplierName(details.getSupplierName());
        existing.setContactPerson(details.getContactPerson());
        existing.setPhone(details.getPhone());
        existing.setEmail(details.getEmail());
        existing.setAddress(details.getAddress());
        existing.setTaxNumber(details.getTaxNumber());
        if (details.getStatus() != null) {
            existing.setStatus(details.getStatus());
        }
        return supplierRepository.save(existing);
    }

    public void deleteSupplier(Long id) {
        ChemicalSupplier supplier = getSupplierById(id);
        supplierRepository.delete(supplier);
    }
}
