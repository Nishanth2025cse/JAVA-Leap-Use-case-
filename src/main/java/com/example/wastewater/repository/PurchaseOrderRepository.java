package com.example.wastewater.repository;

import com.example.wastewater.entity.PurchaseOrder;
import com.example.wastewater.enums.POStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {
    List<PurchaseOrder> findBySupplierId(Long supplierId);
    List<PurchaseOrder> findByStatus(POStatus status);
}
