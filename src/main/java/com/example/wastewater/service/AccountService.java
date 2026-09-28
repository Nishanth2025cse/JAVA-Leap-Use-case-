package com.example.wastewater.service;

import com.example.wastewater.entity.Account;
import com.example.wastewater.enums.AccountType;
import com.example.wastewater.exception.BadRequestException;
import com.example.wastewater.exception.ResourceNotFoundException;
import com.example.wastewater.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public Account getAccountById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + id));
    }

    public Account createAccount(Account account) {
        if (accountRepository.findByAccountCode(account.getAccountCode()).isPresent()) {
            throw new BadRequestException("Account code already exists: " + account.getAccountCode());
        }
        if (account.getCurrentBalance() == null) {
            account.setCurrentBalance(account.getOpeningBalance());
        }
        return accountRepository.save(account);
    }

    public Account updateAccount(Long id, Account details) {
        Account existing = getAccountById(id);
        existing.setAccountName(details.getAccountName());
        existing.setAccountType(details.getAccountType());
        existing.setActive(details.isActive());
        return accountRepository.save(existing);
    }

    public List<Account> getAccountsByType(AccountType type) {
        return accountRepository.findByAccountType(type);
    }
}
