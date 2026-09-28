package com.example.wastewater.controller;

import com.example.wastewater.entity.CustomerInvoice;
import com.example.wastewater.service.CustomerInvoiceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer-invoices")
@CrossOrigin(origins = "*")
public class CustomerInvoiceController {

    private final CustomerInvoiceService invoiceService;

    public CustomerInvoiceController(CustomerInvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @GetMapping
    public ResponseEntity<List<CustomerInvoice>> getAllInvoices() {
        return ResponseEntity.ok(invoiceService.getAllInvoices());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerInvoice> getInvoiceById(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.getInvoiceById(id));
    }

    @GetMapping("/factory/{factoryId}")
    public ResponseEntity<List<CustomerInvoice>> getInvoicesByFactory(@PathVariable Long factoryId) {
        return ResponseEntity.ok(invoiceService.getInvoicesByFactory(factoryId));
    }

    @PostMapping
    public ResponseEntity<CustomerInvoice> createInvoice(@Valid @RequestBody CustomerInvoice invoice) {
        CustomerInvoice created = invoiceService.createInvoice(invoice);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
}
