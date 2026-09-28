package com.example.wastewater.repository;

import com.example.wastewater.entity.Account;
import com.example.wastewater.enums.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByAccountCode(String accountCode);
    List<Account> findByAccountType(AccountType accountType);
    Optional<Account> findByAccountName(String accountName);
}
