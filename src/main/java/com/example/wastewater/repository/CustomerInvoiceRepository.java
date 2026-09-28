package com.example.wastewater.repository;

import com.example.wastewater.entity.CustomerInvoice;
import com.example.wastewater.enums.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface CustomerInvoiceRepository extends JpaRepository<CustomerInvoice, Long> {
    Optional<CustomerInvoice> findByInvoiceNumber(String invoiceNumber);
    List<CustomerInvoice> findByFactoryId(Long factoryId);
    List<CustomerInvoice> findByStatus(InvoiceStatus status);

    @Query("SELECT COUNT(i) FROM CustomerInvoice i WHERE i.status IN ('ISSUED', 'PARTIALLY_PAID', 'OVERDUE')")
    long countPendingInvoices();

    @Query("SELECT SUM(i.balanceAmount) FROM CustomerInvoice i WHERE i.status != 'PAID'")
    Double getTotalPendingReceivables();

    @Query("SELECT SUM(i.treatmentAmount) FROM CustomerInvoice i")
    Double getTotalTreatmentRevenue();

    List<CustomerInvoice> findTop10ByOrderByInvoiceDateDesc();
}
