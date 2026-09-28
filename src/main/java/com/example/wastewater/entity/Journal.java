package com.example.wastewater.entity;

import com.example.wastewater.enums.JournalType;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "journals")
public class Journal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JournalType journalType;

    private LocalDateTime transactionDate;

    private String referenceNumber;

    private String description;

    @OneToMany(mappedBy = "journal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JournalEntry> entries = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (this.transactionDate == null) {
            this.transactionDate = LocalDateTime.now();
        }
    }

    public Journal() {}

    public Journal(JournalType journalType, String referenceNumber, String description) {
        this.journalType = journalType;
        this.referenceNumber = referenceNumber;
        this.description = description;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public JournalType getJournalType() { return journalType; }
    public void setJournalType(JournalType journalType) { this.journalType = journalType; }

    public LocalDateTime getTransactionDate() { return transactionDate; }
    public void setTransactionDate(LocalDateTime transactionDate) { this.transactionDate = transactionDate; }

    public String getReferenceNumber() { return referenceNumber; }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<JournalEntry> getEntries() { return entries; }
    public void setEntries(List<JournalEntry> entries) { this.entries = entries; }

    public void addEntry(JournalEntry entry) {
        entry.setJournal(this);
        this.entries.add(entry);
    }
}
