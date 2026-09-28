package com.example.wastewater.controller;

import com.example.wastewater.entity.VendorBill;
import com.example.wastewater.service.VendorBillService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vendor-bills")
@CrossOrigin(origins = "*")
public class VendorBillController {

    private final VendorBillService vendorBillService;

    public VendorBillController(VendorBillService vendorBillService) {
        this.vendorBillService = vendorBillService;
    }

    @GetMapping
    public ResponseEntity<List<VendorBill>> getAllVendorBills() {
        return ResponseEntity.ok(vendorBillService.getAllVendorBills());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VendorBill> getVendorBillById(@PathVariable Long id) {
        return ResponseEntity.ok(vendorBillService.getVendorBillById(id));
    }

    @PostMapping
    public ResponseEntity<VendorBill> createVendorBill(@Valid @RequestBody VendorBill bill) {
        VendorBill created = vendorBillService.createVendorBill(bill);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
}
