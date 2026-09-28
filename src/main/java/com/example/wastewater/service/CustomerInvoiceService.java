package com.example.wastewater.service;

import com.example.wastewater.entity.Account;
import com.example.wastewater.entity.CustomerInvoice;
import com.example.wastewater.entity.EnvironmentalViolation;
import com.example.wastewater.entity.IndustrialFactory;
import com.example.wastewater.entity.Journal;
import com.example.wastewater.entity.JournalEntry;
import com.example.wastewater.entity.ServiceOrder;
import com.example.wastewater.enums.InvoiceStatus;
import com.example.wastewater.enums.JournalType;
import com.example.wastewater.enums.ServiceOrderStatus;
import com.example.wastewater.enums.ViolationStatus;
import com.example.wastewater.exception.ResourceNotFoundException;
import com.example.wastewater.repository.AccountRepository;
import com.example.wastewater.repository.CustomerInvoiceRepository;
import com.example.wastewater.repository.EnvironmentalViolationRepository;
import com.example.wastewater.repository.IndustrialFactoryRepository;
import com.example.wastewater.repository.ServiceOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Business service for managing customer invoices, calculating environmental
 * fine surcharges for non-compliant COD telemetry, and posting journal entries.
 */
@Service
public class CustomerInvoiceService {

    private final CustomerInvoiceRepository invoiceRepository;
    private final IndustrialFactoryRepository factoryRepository;
    private final ServiceOrderRepository serviceOrderRepository;
    private final EnvironmentalViolationRepository violationRepository;
    private final AccountRepository accountRepository;
    private final JournalService journalService;

    public CustomerInvoiceService(CustomerInvoiceRepository invoiceRepository,
                                  IndustrialFactoryRepository factoryRepository,
                                  ServiceOrderRepository serviceOrderRepository,
                                  EnvironmentalViolationRepository violationRepository,
                                  AccountRepository accountRepository,
                                  JournalService journalService) {
        this.invoiceRepository = invoiceRepository;
        this.factoryRepository = factoryRepository;
        this.serviceOrderRepository = serviceOrderRepository;
        this.violationRepository = violationRepository;
        this.accountRepository = accountRepository;
        this.journalService = journalService;
    }

    public List<CustomerInvoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    public CustomerInvoice getInvoiceById(Long id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer invoice not found with id: " + id));
    }

    public List<CustomerInvoice> getInvoicesByFactory(Long factoryId) {
        return invoiceRepository.findByFactoryId(factoryId);
    }

    /**
     * Creates a new customer invoice. Automatically fetches any open COD environmental
     * violation fines for the factory and applies them as a compliance surcharge.
     */
    @Transactional
    public CustomerInvoice createInvoice(CustomerInvoice invoice) {
        Long factoryId = invoice.getFactory().getId();
        IndustrialFactory factory = factoryRepository.findById(factoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Industrial factory not found with id: " + factoryId));

        invoice.setFactory(factory);

        // Process linked service order if provided
        if (invoice.getServiceOrder() != null && invoice.getServiceOrder().getId() != null) {
            ServiceOrder serviceOrder = serviceOrderRepository.findById(invoice.getServiceOrder().getId()).orElse(null);
            if (serviceOrder != null) {
                invoice.setServiceOrder(serviceOrder);
                if (invoice.getTreatmentAmount() == null || invoice.getTreatmentAmount() == 0.0) {
                    invoice.setTreatmentAmount(serviceOrder.getTotalAmount());
                }
                serviceOrder.setStatus(ServiceOrderStatus.INVOICED);
                serviceOrderRepository.save(serviceOrder);
            }
        }

        double baseTreatment = invoice.getTreatmentAmount() != null ? invoice.getTreatmentAmount() : 5000.0;
        invoice.setTreatmentAmount(baseTreatment);

        // Fetch all open environmental violations for this factory
        List<EnvironmentalViolation> openViolations = violationRepository.findByFactoryIdAndStatus(factoryId, ViolationStatus.OPEN);
        double totalSurcharge = openViolations.stream()
                .mapToDouble(EnvironmentalViolation::getFineAmount)
                .sum();
        invoice.setSurchargeAmount(totalSurcharge);

        // Compute total invoice breakdown (18% tax)
        double taxableSubtotal = baseTreatment + totalSurcharge;
        double taxAmount = taxableSubtotal * 0.18;
        invoice.setTaxAmount(taxAmount);

        double totalAmount = taxableSubtotal + taxAmount;
        invoice.setTotalAmount(totalAmount);
        invoice.setPaidAmount(0.0);
        invoice.setBalanceAmount(totalAmount);
        invoice.setStatus(InvoiceStatus.ISSUED);

        if (invoice.getInvoiceNumber() == null || invoice.getInvoiceNumber().isEmpty()) {
            invoice.setInvoiceNumber("INV-" + LocalDate.now().getYear() + "-" + (System.currentTimeMillis() % 10000));
        }

        CustomerInvoice savedInvoice = invoiceRepository.save(invoice);

        // Update violation status to INVOICED
        for (EnvironmentalViolation violation : openViolations) {
            violation.setStatus(ViolationStatus.INVOICED);
            violationRepository.save(violation);
        }

        // Post balanced accounting journal transaction
        postInvoiceJournalEntry(savedInvoice, factory.getFactoryName(), totalAmount, baseTreatment + taxAmount, totalSurcharge);

        return savedInvoice;
    }

    private void postInvoiceJournalEntry(CustomerInvoice invoice, String factoryName, double totalAmount, double feeCredit, double surchargeCredit) {
        try {
            Account receivablesAcc = accountRepository.findByAccountCode("1020").orElse(null);
            Account feeIncomeAcc = accountRepository.findByAccountName("Effluent Treatment Fees").orElse(null);
            Account fineIncomeAcc = accountRepository.findByAccountName("Compliance Fines").orElse(null);

            if (receivablesAcc != null && feeIncomeAcc != null) {
                Journal journal = new Journal(JournalType.SALES, invoice.getInvoiceNumber(), "Customer Invoice for " + factoryName);
                journal.addEntry(new JournalEntry(receivablesAcc, totalAmount, 0.0));
                journal.addEntry(new JournalEntry(feeIncomeAcc, 0.0, feeCredit));

                if (surchargeCredit > 0 && fineIncomeAcc != null) {
                    journal.addEntry(new JournalEntry(fineIncomeAcc, 0.0, surchargeCredit));
                }
                journalService.createJournalTransaction(journal);
            }
        } catch (Exception e) {
            // Silently handle accounting entry fallback if accounts are unseeded
        }
    }
}
