package com.example.wastewater.controller;

import com.example.wastewater.entity.ChemicalInventory;
import com.example.wastewater.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory/chemicals")
@CrossOrigin(origins = "*")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping
    public ResponseEntity<List<ChemicalInventory>> getAllStock() {
        return ResponseEntity.ok(inventoryService.getAllStock());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChemicalInventory> getStockById(@PathVariable Long id) {
        return ResponseEntity.ok(inventoryService.getStockById(id));
    }

    @GetMapping("/low-stock")
    public ResponseEntity<List<ChemicalInventory>> getLowStockItems() {
        return ResponseEntity.ok(inventoryService.getLowStockItems());
    }

    @GetMapping("/search")
    public ResponseEntity<List<ChemicalInventory>> searchStock(@RequestParam(required = false) String query) {
        return ResponseEntity.ok(inventoryService.searchStock(query));
    }

    @PostMapping("/add")
    public ResponseEntity<ChemicalInventory> addStock(@RequestParam Long productId,
                                                       @RequestParam Double quantity,
                                                       @RequestParam(required = false) Double unitCost) {
        return ResponseEntity.ok(inventoryService.addStock(productId, quantity, unitCost));
    }

    @PostMapping("/reduce")
    public ResponseEntity<ChemicalInventory> reduceStock(@RequestParam Long productId,
                                                          @RequestParam Double quantity) {
        return ResponseEntity.ok(inventoryService.reduceStock(productId, quantity));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ChemicalInventory> updateInventory(@PathVariable Long id, @Valid @RequestBody ChemicalInventory inventory) {
        return ResponseEntity.ok(inventoryService.updateInventory(id, inventory));
    }
}
