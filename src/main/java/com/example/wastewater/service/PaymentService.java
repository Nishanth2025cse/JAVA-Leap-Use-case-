package com.example.wastewater.service;

import com.example.wastewater.entity.Account;
import com.example.wastewater.entity.CustomerInvoice;
import com.example.wastewater.entity.EnvironmentalViolation;
import com.example.wastewater.entity.Journal;
import com.example.wastewater.entity.JournalEntry;
import com.example.wastewater.entity.Payment;
import com.example.wastewater.enums.InvoiceStatus;
import com.example.wastewater.enums.JournalType;
import com.example.wastewater.enums.PaymentMethod;
import com.example.wastewater.enums.ViolationStatus;
import com.example.wastewater.exception.BadRequestException;
import com.example.wastewater.exception.ResourceNotFoundException;
import com.example.wastewater.repository.AccountRepository;
import com.example.wastewater.repository.CustomerInvoiceRepository;
import com.example.wastewater.repository.EnvironmentalViolationRepository;
import com.example.wastewater.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service class handling customer payment processing, invoice balance reconciliation,
 * violation status updates, and double-entry bank deposit journal entries.
 */
@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final CustomerInvoiceRepository invoiceRepository;
    private final EnvironmentalViolationRepository violationRepository;
    private final AccountRepository accountRepository;
    private final JournalService journalService;

    public PaymentService(PaymentRepository paymentRepository,
                          CustomerInvoiceRepository invoiceRepository,
                          EnvironmentalViolationRepository violationRepository,
                          AccountRepository accountRepository,
                          JournalService journalService) {
        this.paymentRepository = paymentRepository;
        this.invoiceRepository = invoiceRepository;
        this.violationRepository = violationRepository;
        this.accountRepository = accountRepository;
        this.journalService = journalService;
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public List<Payment> getPaymentsByInvoice(Long invoiceId) {
        return paymentRepository.findByInvoiceId(invoiceId);
    }

    /**
     * Records a customer payment, updates invoice paid and remaining balances,
     * updates linked violation status upon full settlement, and posts accounting journals.
     */
    @Transactional
    public Payment recordPayment(Payment payment) {
        Long invoiceId = payment.getInvoice().getId();
        CustomerInvoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer invoice not found with id: " + invoiceId));

        if (payment.getAmount() <= 0) {
            throw new BadRequestException("Payment amount must be greater than zero.");
        }

        if (payment.getAmount() > invoice.getBalanceAmount() + 0.01) {
            throw new BadRequestException("Payment amount (" + payment.getAmount() + 
                    ") cannot exceed remaining invoice balance (" + invoice.getBalanceAmount() + ").");
        }

        payment.setInvoice(invoice);
        Payment savedPayment = paymentRepository.save(payment);

        double newPaid = invoice.getPaidAmount() + payment.getAmount();
        double newBalance = invoice.getTotalAmount() - newPaid;

        invoice.setPaidAmount(newPaid);
        invoice.setBalanceAmount(Math.max(0.0, newBalance));

        if (invoice.getBalanceAmount() <= 0.01) {
            invoice.setStatus(InvoiceStatus.PAID);
            
            // Mark linked violations as PAID once invoice is fully settled
            List<EnvironmentalViolation> violations = violationRepository.findByFactoryIdAndStatus(invoice.getFactory().getId(), ViolationStatus.INVOICED);
            for (EnvironmentalViolation v : violations) {
                v.setStatus(ViolationStatus.PAID);
                violationRepository.save(v);
            }
        } else {
            invoice.setStatus(InvoiceStatus.PARTIALLY_PAID);
        }

        invoiceRepository.save(invoice);

        // Record Bank / Cash Journal Entry
        postPaymentJournalEntry(payment, invoice);

        return savedPayment;
    }

    private void postPaymentJournalEntry(Payment payment, CustomerInvoice invoice) {
        try {
            Account bankAcc = accountRepository.findByAccountName(
                payment.getPaymentMethod() == PaymentMethod.CASH ? "Cash" : "Bank Account").orElse(null);
            Account receivablesAcc = accountRepository.findByAccountCode("1020").orElse(null);

            if (bankAcc != null && receivablesAcc != null) {
                Journal journal = new Journal(JournalType.BANK, payment.getReferenceNumber(), "Customer Payment for Invoice #" + invoice.getInvoiceNumber());
                journal.addEntry(new JournalEntry(bankAcc, payment.getAmount(), 0.0));
                journal.addEntry(new JournalEntry(receivablesAcc, 0.0, payment.getAmount()));
                journalService.createJournalTransaction(journal);
            }
        } catch (Exception e) {
            // Silently fallback if accounts are uninitialized
        }
    }
}
