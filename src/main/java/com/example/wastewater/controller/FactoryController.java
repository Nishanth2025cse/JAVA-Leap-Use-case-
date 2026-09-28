package com.example.wastewater.controller;

import com.example.wastewater.entity.IndustrialFactory;
import com.example.wastewater.service.FactoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wastewater/plants")
@CrossOrigin(origins = "*")
public class FactoryController {

    private final FactoryService factoryService;

    public FactoryController(FactoryService factoryService) {
        this.factoryService = factoryService;
    }

    @GetMapping
    public ResponseEntity<List<IndustrialFactory>> getAllFactories() {
        return ResponseEntity.ok(factoryService.getAllFactories());
    }

    @GetMapping("/{id}")
    public ResponseEntity<IndustrialFactory> getFactoryById(@PathVariable Long id) {
        return ResponseEntity.ok(factoryService.getFactoryById(id));
    }

    @PostMapping
    public ResponseEntity<IndustrialFactory> createFactory(@Valid @RequestBody IndustrialFactory factory) {
        IndustrialFactory created = factoryService.createFactory(factory);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<IndustrialFactory> updateFactory(@PathVariable Long id, @Valid @RequestBody IndustrialFactory factory) {
        IndustrialFactory updated = factoryService.updateFactory(id, factory);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFactory(@PathVariable Long id) {
        factoryService.deleteFactory(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<IndustrialFactory>> searchFactories(@RequestParam(required = false) String query) {
        return ResponseEntity.ok(factoryService.searchFactories(query));
    }
}
