package com.example.wastewater.service;

import com.example.wastewater.entity.ChemicalInventory;
import com.example.wastewater.entity.Product;
import com.example.wastewater.exception.BadRequestException;
import com.example.wastewater.exception.ResourceNotFoundException;
import com.example.wastewater.repository.ChemicalInventoryRepository;
import com.example.wastewater.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class InventoryService {

    private final ChemicalInventoryRepository inventoryRepository;
    private final ProductRepository productRepository;

    public InventoryService(ChemicalInventoryRepository inventoryRepository, ProductRepository productRepository) {
        this.inventoryRepository = inventoryRepository;
        this.productRepository = productRepository;
    }

    public List<ChemicalInventory> getAllStock() {
        return inventoryRepository.findAll();
    }

    public ChemicalInventory getStockById(Long id) {
        return inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Chemical inventory record not found with id: " + id));
    }

    public ChemicalInventory getStockByProductId(Long productId) {
        return inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("No inventory found for product id: " + productId));
    }

    public List<ChemicalInventory> getLowStockItems() {
        return inventoryRepository.findLowStockItems();
    }

    public List<ChemicalInventory> searchStock(String query) {
        if (query == null || query.trim().isEmpty()) {
            return inventoryRepository.findAll();
        }
        return inventoryRepository.searchStock(query.trim());
    }

    @Transactional
    public ChemicalInventory addStock(Long productId, Double quantityToAdd, Double unitCost) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        ChemicalInventory inventory = inventoryRepository.findByProductId(productId)
                .orElseGet(() -> {
                    ChemicalInventory inv = new ChemicalInventory();
                    inv.setProduct(product);
                    inv.setQuantity(0.0);
                    inv.setReorderLevel(50.0);
                    inv.setUnitCost(unitCost != null ? unitCost : product.getPrice());
                    return inv;
                });

        inventory.setQuantity(inventory.getQuantity() + quantityToAdd);
        if (unitCost != null && unitCost > 0) {
            inventory.setUnitCost(unitCost);
        }
        inventory.setLastUpdated(LocalDateTime.now());
        return inventoryRepository.save(inventory);
    }

    @Transactional
    public ChemicalInventory reduceStock(Long productId, Double quantityToReduce) {
        ChemicalInventory inventory = getStockByProductId(productId);
        if (inventory.getQuantity() < quantityToReduce) {
            throw new BadRequestException("Insufficient chemical stock! Available: " + inventory.getQuantity() + ", requested: " + quantityToReduce);
        }
        inventory.setQuantity(inventory.getQuantity() - quantityToReduce);
        inventory.setLastUpdated(LocalDateTime.now());
        return inventoryRepository.save(inventory);
    }

    public ChemicalInventory updateInventory(Long id, ChemicalInventory details) {
        ChemicalInventory existing = getStockById(id);
        existing.setQuantity(details.getQuantity());
        existing.setReorderLevel(details.getReorderLevel());
        existing.setUnitCost(details.getUnitCost());
        existing.setLastUpdated(LocalDateTime.now());
        return inventoryRepository.save(existing);
    }
}
