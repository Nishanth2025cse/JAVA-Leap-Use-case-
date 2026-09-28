package com.example.wastewater.repository;

import com.example.wastewater.entity.ChemicalInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChemicalInventoryRepository extends JpaRepository<ChemicalInventory, Long> {
    Optional<ChemicalInventory> findByProductId(Long productId);

    @Query("SELECT c FROM ChemicalInventory c WHERE c.quantity <= c.reorderLevel")
    List<ChemicalInventory> findLowStockItems();

    @Query("SELECT c FROM ChemicalInventory c WHERE " +
           "LOWER(c.product.productName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(c.product.productCode) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<ChemicalInventory> searchStock(@Param("query") String query);

    @Query("SELECT SUM(c.quantity * c.unitCost) FROM ChemicalInventory c")
    Double getTotalInventoryValue();
}
