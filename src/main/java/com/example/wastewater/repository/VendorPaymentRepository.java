package com.example.wastewater.repository;

import com.example.wastewater.entity.VendorPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VendorPaymentRepository extends JpaRepository<VendorPayment, Long> {
    List<VendorPayment> findByVendorBillId(Long vendorBillId);
}
