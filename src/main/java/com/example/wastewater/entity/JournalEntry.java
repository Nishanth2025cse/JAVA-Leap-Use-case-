package com.example.wastewater.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;

@Entity
@Table(name = "journal_entries")
public class JournalEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "journal_id", nullable = false)
    @JsonIgnore
    private Journal journal;

    @ManyToOne(optional = false)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @PositiveOrZero
    private Double debit = 0.0;

    @PositiveOrZero
    private Double credit = 0.0;

    public JournalEntry() {}

    public JournalEntry(Account account, Double debit, Double credit) {
        this.account = account;
        this.debit = debit != null ? debit : 0.0;
        this.credit = credit != null ? credit : 0.0;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Journal getJournal() { return journal; }
    public void setJournal(Journal journal) { this.journal = journal; }

    public Account getAccount() { return account; }
    public void setAccount(Account account) { this.account = account; }

    public Double getDebit() { return debit; }
    public void setDebit(Double debit) { this.debit = debit; }

    public Double getCredit() { return credit; }
    public void setCredit(Double credit) { this.credit = credit; }
}
