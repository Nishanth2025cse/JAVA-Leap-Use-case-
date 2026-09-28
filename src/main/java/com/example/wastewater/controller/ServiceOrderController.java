package com.example.wastewater.controller;

import com.example.wastewater.entity.ServiceOrder;
import com.example.wastewater.enums.ServiceOrderStatus;
import com.example.wastewater.service.ServiceOrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/service-orders")
@CrossOrigin(origins = "*")
public class ServiceOrderController {

    private final ServiceOrderService serviceOrderService;

    public ServiceOrderController(ServiceOrderService serviceOrderService) {
        this.serviceOrderService = serviceOrderService;
    }

    @GetMapping
    public ResponseEntity<List<ServiceOrder>> getAllServiceOrders() {
        return ResponseEntity.ok(serviceOrderService.getAllServiceOrders());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceOrder> getServiceOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(serviceOrderService.getServiceOrderById(id));
    }

    @PostMapping
    public ResponseEntity<ServiceOrder> createServiceOrder(@Valid @RequestBody ServiceOrder order) {
        ServiceOrder created = serviceOrderService.createServiceOrder(order);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ServiceOrder> updateStatus(@PathVariable Long id, @RequestParam ServiceOrderStatus status) {
        return ResponseEntity.ok(serviceOrderService.updateServiceOrderStatus(id, status));
    }
}
