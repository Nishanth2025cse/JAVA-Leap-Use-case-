package com.example.wastewater.service;

import com.example.wastewater.entity.Account;
import com.example.wastewater.entity.AnalyticAccount;
import com.example.wastewater.entity.Journal;
import com.example.wastewater.entity.JournalEntry;
import com.example.wastewater.entity.VendorBill;
import com.example.wastewater.entity.VendorPayment;
import com.example.wastewater.enums.BillStatus;
import com.example.wastewater.enums.JournalType;
import com.example.wastewater.enums.PaymentMethod;
import com.example.wastewater.exception.BadRequestException;
import com.example.wastewater.exception.ResourceNotFoundException;
import com.example.wastewater.repository.AccountRepository;
import com.example.wastewater.repository.AnalyticAccountRepository;
import com.example.wastewater.repository.VendorBillRepository;
import com.example.wastewater.repository.VendorPaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Business service for processing chemical vendor bill payments, updating vendor accounts payable,
 * posting double-entry purchase journals, and tracking treatment facility budget spending.
 */
@Service
public class VendorPaymentService {

    private final VendorPaymentRepository vendorPaymentRepository;
    private final VendorBillRepository vendorBillRepository;
    private final AccountRepository accountRepository;
    private final JournalService journalService;
    private final BudgetService budgetService;
    private final AnalyticAccountRepository analyticAccountRepository;

    public VendorPaymentService(VendorPaymentRepository vendorPaymentRepository,
                                VendorBillRepository vendorBillRepository,
                                AccountRepository accountRepository,
                                JournalService journalService,
                                BudgetService budgetService,
                                AnalyticAccountRepository analyticAccountRepository) {
        this.vendorPaymentRepository = vendorPaymentRepository;
        this.vendorBillRepository = vendorBillRepository;
        this.accountRepository = accountRepository;
        this.journalService = journalService;
        this.budgetService = budgetService;
        this.analyticAccountRepository = analyticAccountRepository;
    }

    public List<VendorPayment> getAllVendorPayments() {
        return vendorPaymentRepository.findAll();
    }

    public List<VendorPayment> getPaymentsByBill(Long vendorBillId) {
        return vendorPaymentRepository.findByVendorBillId(vendorBillId);
    }

    /**
     * Records payment for a vendor bill, updates payables balance, records actual plant
     * budget expenses, and posts double-entry journal entries.
     */
    @Transactional
    public VendorPayment recordVendorPayment(VendorPayment payment) {
        Long billId = payment.getVendorBill().getId();
        VendorBill bill = vendorBillRepository.findById(billId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor bill not found with id: " + billId));

        if (payment.getAmount() <= 0) {
            throw new BadRequestException("Payment amount must be positive.");
        }

        if (payment.getAmount() > bill.getBalanceAmount() + 0.01) {
            throw new BadRequestException("Payment amount (" + payment.getAmount() + 
                    ") cannot exceed remaining bill balance (" + bill.getBalanceAmount() + ").");
        }

        payment.setVendorBill(bill);
        VendorPayment savedPayment = vendorPaymentRepository.save(payment);

        double newPaid = bill.getPaidAmount() + payment.getAmount();
        double newBalance = bill.getTotalAmount() - newPaid;

        bill.setPaidAmount(newPaid);
        bill.setBalanceAmount(Math.max(0.0, newBalance));

        if (bill.getBalanceAmount() <= 0.01) {
            bill.setStatus(BillStatus.PAID);
        } else {
            bill.setStatus(BillStatus.PARTIALLY_PAID);
        }

        vendorBillRepository.save(bill);

        // Update Plant Operational Budget Spending
        List<AnalyticAccount> facilities = analyticAccountRepository.findAll();
        if (!facilities.isEmpty()) {
            budgetService.recordExpense(facilities.get(0).getId(), payment.getAmount());
        }

        // Post Journal Entry
        postVendorPaymentJournalEntry(payment, bill);

        return savedPayment;
    }

    private void postVendorPaymentJournalEntry(VendorPayment payment, VendorBill bill) {
        try {
            Account payablesAcc = accountRepository.findByAccountName("Chemical Payables").orElse(null);
            Account bankAcc = accountRepository.findByAccountName(
                payment.getPaymentMethod() == PaymentMethod.CASH ? "Cash" : "Bank Account").orElse(null);

            if (payablesAcc != null && bankAcc != null) {
                Journal journal = new Journal(JournalType.PURCHASE, payment.getReferenceNumber(), "Vendor Payment for Bill #" + bill.getBillNumber());
                journal.addEntry(new JournalEntry(payablesAcc, payment.getAmount(), 0.0));
                journal.addEntry(new JournalEntry(bankAcc, 0.0, payment.getAmount()));
                journalService.createJournalTransaction(journal);
            }
        } catch (Exception e) {
            // Silently fallback if accounts are uninitialized
        }
    }
}
