package com.example.wastewater.service;

import com.example.wastewater.entity.ChemicalSupplier;
import com.example.wastewater.entity.Product;
import com.example.wastewater.entity.PurchaseOrder;
import com.example.wastewater.entity.PurchaseOrderItem;
import com.example.wastewater.entity.VendorBill;
import com.example.wastewater.enums.BillStatus;
import com.example.wastewater.enums.POStatus;
import com.example.wastewater.exception.ResourceNotFoundException;
import com.example.wastewater.repository.ChemicalSupplierRepository;
import com.example.wastewater.repository.ProductRepository;
import com.example.wastewater.repository.PurchaseOrderRepository;
import com.example.wastewater.repository.VendorBillRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Service managing chemical agent purchase orders, automatic inventory quantity adjustments,
 * and automatic vendor bill creation upon goods receipt.
 */
@Service
public class PurchaseOrderService {

    private final PurchaseOrderRepository poRepository;
    private final ChemicalSupplierRepository supplierRepository;
    private final ProductRepository productRepository;
    private final InventoryService inventoryService;
    private final VendorBillRepository vendorBillRepository;

    public PurchaseOrderService(PurchaseOrderRepository poRepository,
                                ChemicalSupplierRepository supplierRepository,
                                ProductRepository productRepository,
                                InventoryService inventoryService,
                                VendorBillRepository vendorBillRepository) {
        this.poRepository = poRepository;
        this.supplierRepository = supplierRepository;
        this.productRepository = productRepository;
        this.inventoryService = inventoryService;
        this.vendorBillRepository = vendorBillRepository;
    }

    public List<PurchaseOrder> getAllPurchaseOrders() {
        return poRepository.findAll();
    }

    public PurchaseOrder getPurchaseOrderById(Long id) {
        return poRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase order not found with id: " + id));
    }

    @Transactional
    public PurchaseOrder createPurchaseOrder(PurchaseOrder order) {
        Long supplierId = order.getSupplier().getId();
        ChemicalSupplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new ResourceNotFoundException("Chemical supplier not found with id: " + supplierId));

        order.setSupplier(supplier);
        if (order.getOrderDate() == null) {
            order.setOrderDate(LocalDate.now());
        }

        double total = 0.0;
        if (order.getItems() != null && !order.getItems().isEmpty()) {
            for (PurchaseOrderItem item : order.getItems()) {
                item.setPurchaseOrder(order);
                Long prodId = item.getProduct().getId();
                Product prod = productRepository.findById(prodId)
                        .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + prodId));
                item.setProduct(prod);
                if (item.getUnitPrice() == null) {
                    item.setUnitPrice(prod.getPrice());
                }
                double itemTotal = item.getQuantity() * item.getUnitPrice();
                item.setTotalPrice(itemTotal);
                total += itemTotal;
            }
        }
        order.setTotalAmount(total);
        if (order.getStatus() == null) {
            order.setStatus(POStatus.DRAFT);
        }

        return poRepository.save(order);
    }

    /**
     * Updates Purchase Order status. When status transitions to RECEIVED,
     * inventory stock is automatically incremented and a Vendor Bill is generated.
     */
    @Transactional
    public PurchaseOrder updatePOStatus(Long id, POStatus status) {
        PurchaseOrder order = getPurchaseOrderById(id);
        POStatus previousStatus = order.getStatus();

        if (previousStatus == status) {
            return order;
        }

        order.setStatus(status);
        PurchaseOrder updated = poRepository.save(order);

        // When status becomes RECEIVED, automatically update inventory & create vendor bill
        if (status == POStatus.RECEIVED && previousStatus != POStatus.RECEIVED) {
            for (PurchaseOrderItem item : order.getItems()) {
                inventoryService.addStock(item.getProduct().getId(), item.getQuantity(), item.getUnitPrice());
            }

            createVendorBillForPO(order);
        }

        return updated;
    }

    private void createVendorBillForPO(PurchaseOrder order) {
        VendorBill bill = new VendorBill();
        bill.setSupplier(order.getSupplier());
        bill.setPurchaseOrder(order);
        bill.setBillNumber("VB-" + LocalDate.now().getYear() + "-" + (System.currentTimeMillis() % 10000));
        bill.setBillDate(LocalDate.now());
        bill.setDueDate(LocalDate.now().plusDays(30));
        bill.setTotalAmount(order.getTotalAmount());
        bill.setPaidAmount(0.0);
        bill.setBalanceAmount(order.getTotalAmount());
        bill.setStatus(BillStatus.UNPAID);
        vendorBillRepository.save(bill);
    }
}
