package com.example.wastewater.service;

import com.example.wastewater.entity.Account;
import com.example.wastewater.entity.Journal;
import com.example.wastewater.entity.JournalEntry;
import com.example.wastewater.enums.JournalType;
import com.example.wastewater.exception.BadRequestException;
import com.example.wastewater.exception.ResourceNotFoundException;
import com.example.wastewater.repository.AccountRepository;
import com.example.wastewater.repository.JournalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class JournalService {

    private final JournalRepository journalRepository;
    private final AccountRepository accountRepository;

    public JournalService(JournalRepository journalRepository, AccountRepository accountRepository) {
        this.journalRepository = journalRepository;
        this.accountRepository = accountRepository;
    }

    public List<Journal> getAllJournals() {
        return journalRepository.findAll();
    }

    public List<Journal> getRecentJournals() {
        return journalRepository.findTop20ByOrderByTransactionDateDesc();
    }

    @Transactional
    public Journal createJournalTransaction(Journal journal) {
        double totalDebit = 0.0;
        double totalCredit = 0.0;

        if (journal.getEntries() == null || journal.getEntries().isEmpty()) {
            throw new BadRequestException("Journal transaction must contain at least one entry.");
        }

        for (JournalEntry entry : journal.getEntries()) {
            entry.setJournal(journal);
            Long accId = entry.getAccount().getId();
            Account acc = accountRepository.findById(accId)
                    .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + accId));
            entry.setAccount(acc);

            double dr = entry.getDebit() != null ? entry.getDebit() : 0.0;
            double cr = entry.getCredit() != null ? entry.getCredit() : 0.0;
            totalDebit += dr;
            totalCredit += cr;

            // Update Account current balance based on type
            // Debit increases Assets and Expenses, decreases Liabilities and Income
            // Credit increases Liabilities and Income, decreases Assets and Expenses
            switch (acc.getAccountType()) {
                case ASSET:
                case EXPENSE:
                    acc.setCurrentBalance(acc.getCurrentBalance() + dr - cr);
                    break;
                case LIABILITY:
                case INCOME:
                    acc.setCurrentBalance(acc.getCurrentBalance() + cr - dr);
                    break;
            }
            accountRepository.save(acc);
        }

        if (Math.abs(totalDebit - totalCredit) > 0.001) {
            throw new BadRequestException("Unbalanced Journal Transaction! Total Debit (" + totalDebit + 
                    ") must equal Total Credit (" + totalCredit + ").");
        }

        return journalRepository.save(journal);
    }
}
