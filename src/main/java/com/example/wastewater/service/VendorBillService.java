package com.example.wastewater.service;

import com.example.wastewater.entity.ChemicalSupplier;
import com.example.wastewater.entity.VendorBill;
import com.example.wastewater.enums.BillStatus;
import com.example.wastewater.exception.ResourceNotFoundException;
import com.example.wastewater.repository.ChemicalSupplierRepository;
import com.example.wastewater.repository.VendorBillRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class VendorBillService {

    private final VendorBillRepository billRepository;
    private final ChemicalSupplierRepository supplierRepository;

    public VendorBillService(VendorBillRepository billRepository, ChemicalSupplierRepository supplierRepository) {
        this.billRepository = billRepository;
        this.supplierRepository = supplierRepository;
    }

    public List<VendorBill> getAllVendorBills() {
        return billRepository.findAll();
    }

    public VendorBill getVendorBillById(Long id) {
        return billRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor bill not found with id: " + id));
    }

    public VendorBill createVendorBill(VendorBill bill) {
        Long supplierId = bill.getSupplier().getId();
        ChemicalSupplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new ResourceNotFoundException("Chemical supplier not found with id: " + supplierId));

        bill.setSupplier(supplier);
        if (bill.getBillNumber() == null || bill.getBillNumber().isEmpty()) {
            bill.setBillNumber("VB-" + LocalDate.now().getYear() + "-" + (System.currentTimeMillis() % 10000));
        }
        if (bill.getPaidAmount() == null) bill.setPaidAmount(0.0);
        bill.setBalanceAmount(bill.getTotalAmount() - bill.getPaidAmount());
        if (bill.getBalanceAmount() <= 0) {
            bill.setStatus(BillStatus.PAID);
        } else if (bill.getPaidAmount() > 0) {
            bill.setStatus(BillStatus.PARTIALLY_PAID);
        } else {
            bill.setStatus(BillStatus.UNPAID);
        }
        return billRepository.save(bill);
    }
}
