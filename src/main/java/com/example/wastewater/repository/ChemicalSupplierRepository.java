package com.example.wastewater.repository;

import com.example.wastewater.entity.ChemicalSupplier;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChemicalSupplierRepository extends JpaRepository<ChemicalSupplier, Long> {
    List<ChemicalSupplier> findByStatus(String status);
}
