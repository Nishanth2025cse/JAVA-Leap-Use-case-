package com.example.wastewater.repository;

import com.example.wastewater.entity.ServiceOrder;
import com.example.wastewater.enums.ServiceOrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ServiceOrderRepository extends JpaRepository<ServiceOrder, Long> {
    List<ServiceOrder> findByFactoryId(Long factoryId);
    List<ServiceOrder> findByStatus(ServiceOrderStatus status);
}
