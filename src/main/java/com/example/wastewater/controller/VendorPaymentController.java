package com.example.wastewater.controller;

import com.example.wastewater.entity.VendorPayment;
import com.example.wastewater.service.VendorPaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vendor-payments")
@CrossOrigin(origins = "*")
public class VendorPaymentController {

    private final VendorPaymentService vendorPaymentService;

    public VendorPaymentController(VendorPaymentService vendorPaymentService) {
        this.vendorPaymentService = vendorPaymentService;
    }

    @GetMapping
    public ResponseEntity<List<VendorPayment>> getAllVendorPayments() {
        return ResponseEntity.ok(vendorPaymentService.getAllVendorPayments());
    }

    @GetMapping("/bill/{billId}")
    public ResponseEntity<List<VendorPayment>> getPaymentsByBill(@PathVariable Long billId) {
        return ResponseEntity.ok(vendorPaymentService.getPaymentsByBill(billId));
    }

    @PostMapping
    public ResponseEntity<VendorPayment> recordVendorPayment(@Valid @RequestBody VendorPayment payment) {
        VendorPayment created = vendorPaymentService.recordVendorPayment(payment);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
}
