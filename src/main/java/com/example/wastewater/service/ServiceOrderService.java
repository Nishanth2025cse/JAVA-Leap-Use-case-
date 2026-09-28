package com.example.wastewater.service;

import com.example.wastewater.entity.IndustrialFactory;
import com.example.wastewater.entity.ServiceOrder;
import com.example.wastewater.enums.ServiceOrderStatus;
import com.example.wastewater.exception.ResourceNotFoundException;
import com.example.wastewater.repository.IndustrialFactoryRepository;
import com.example.wastewater.repository.ServiceOrderRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ServiceOrderService {

    private final ServiceOrderRepository serviceOrderRepository;
    private final IndustrialFactoryRepository factoryRepository;

    public ServiceOrderService(ServiceOrderRepository serviceOrderRepository, IndustrialFactoryRepository factoryRepository) {
        this.serviceOrderRepository = serviceOrderRepository;
        this.factoryRepository = factoryRepository;
    }

    public List<ServiceOrder> getAllServiceOrders() {
        return serviceOrderRepository.findAll();
    }

    public ServiceOrder getServiceOrderById(Long id) {
        return serviceOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service order not found with id: " + id));
    }

    public ServiceOrder createServiceOrder(ServiceOrder order) {
        Long factoryId = order.getFactory().getId();
        IndustrialFactory factory = factoryRepository.findById(factoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Industrial factory not found with id: " + factoryId));

        order.setFactory(factory);
        if (order.getOrderDate() == null) {
            order.setOrderDate(LocalDate.now());
        }
        double qty = order.getQuantity() != null ? order.getQuantity() : 1.0;
        double price = order.getUnitPrice() != null ? order.getUnitPrice() : 5000.0;
        order.setTotalAmount(qty * price);
        if (order.getStatus() == null) {
            order.setStatus(ServiceOrderStatus.PENDING);
        }
        return serviceOrderRepository.save(order);
    }

    public ServiceOrder updateServiceOrderStatus(Long id, ServiceOrderStatus status) {
        ServiceOrder order = getServiceOrderById(id);
        order.setStatus(status);
        return serviceOrderRepository.save(order);
    }
}
