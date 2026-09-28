package com.example.wastewater.controller;

import com.example.wastewater.entity.AnalyticAccount;
import com.example.wastewater.service.AnalyticAccountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/analytic-accounts")
@CrossOrigin(origins = "*")
public class AnalyticAccountController {

    private final AnalyticAccountService analyticAccountService;

    public AnalyticAccountController(AnalyticAccountService analyticAccountService) {
        this.analyticAccountService = analyticAccountService;
    }

    @GetMapping
    public ResponseEntity<List<AnalyticAccount>> getAllAnalyticAccounts() {
        return ResponseEntity.ok(analyticAccountService.getAllAnalyticAccounts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnalyticAccount> getById(@PathVariable Long id) {
        return ResponseEntity.ok(analyticAccountService.getById(id));
    }

    @PostMapping
    public ResponseEntity<AnalyticAccount> create(@Valid @RequestBody AnalyticAccount account) {
        AnalyticAccount created = analyticAccountService.create(account);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AnalyticAccount> update(@PathVariable Long id, @Valid @RequestBody AnalyticAccount account) {
        AnalyticAccount updated = analyticAccountService.update(id, account);
        return ResponseEntity.ok(updated);
    }
}
