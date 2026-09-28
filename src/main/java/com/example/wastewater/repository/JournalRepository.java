package com.example.wastewater.repository;

import com.example.wastewater.entity.Journal;
import com.example.wastewater.enums.JournalType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface JournalRepository extends JpaRepository<Journal, Long> {
    List<Journal> findByJournalType(JournalType journalType);
    List<Journal> findTop20ByOrderByTransactionDateDesc();
}
