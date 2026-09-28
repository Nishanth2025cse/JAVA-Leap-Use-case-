package com.example.wastewater.repository;

import com.example.wastewater.entity.VendorBill;
import com.example.wastewater.enums.BillStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface VendorBillRepository extends JpaRepository<VendorBill, Long> {
    Optional<VendorBill> findByBillNumber(String billNumber);
    List<VendorBill> findBySupplierId(Long supplierId);
    List<VendorBill> findByStatus(BillStatus status);

    @Query("SELECT SUM(v.balanceAmount) FROM VendorBill v WHERE v.status != 'PAID'")
    Double getTotalPendingPayables();
}
