package com.example.wastewater.controller;

import com.example.wastewater.dto.BalanceSheetDTO;
import com.example.wastewater.dto.BudgetReportDTO;
import com.example.wastewater.dto.ComplianceReportDTO;
import com.example.wastewater.dto.ProfitLossDTO;
import com.example.wastewater.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/balance-sheet")
    public ResponseEntity<BalanceSheetDTO> getBalanceSheet() {
        return ResponseEntity.ok(reportService.getBalanceSheet());
    }

    @GetMapping("/profit-loss")
    public ResponseEntity<ProfitLossDTO> getProfitLoss() {
        return ResponseEntity.ok(reportService.getProfitLoss());
    }

    @GetMapping("/budget")
    public ResponseEntity<BudgetReportDTO> getBudgetReport() {
        return ResponseEntity.ok(reportService.getBudgetReport());
    }

    @GetMapping("/compliance")
    public ResponseEntity<ComplianceReportDTO> getComplianceReport() {
        return ResponseEntity.ok(reportService.getComplianceReport());
    }
}
