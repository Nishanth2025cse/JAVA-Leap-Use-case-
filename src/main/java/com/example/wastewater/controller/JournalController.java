package com.example.wastewater.controller;

import com.example.wastewater.entity.Journal;
import com.example.wastewater.service.JournalService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/journals")
@CrossOrigin(origins = "*")
public class JournalController {

    private final JournalService journalService;

    public JournalController(JournalService journalService) {
        this.journalService = journalService;
    }

    @GetMapping
    public ResponseEntity<List<Journal>> getAllJournals() {
        return ResponseEntity.ok(journalService.getAllJournals());
    }

    @GetMapping("/recent")
    public ResponseEntity<List<Journal>> getRecentJournals() {
        return ResponseEntity.ok(journalService.getRecentJournals());
    }

    @PostMapping
    public ResponseEntity<Journal> createJournalTransaction(@Valid @RequestBody Journal journal) {
        Journal created = journalService.createJournalTransaction(journal);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
}
