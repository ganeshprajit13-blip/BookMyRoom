package com.library.ebook.controller;

import com.library.ebook.dto.*;
import com.library.ebook.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardStatsDto> getDashboardStats() {
        return ResponseEntity.ok(reportService.getDashboardStats());
    }

    @GetMapping("/balance-sheet")
    public ResponseEntity<BalanceSheetDto> getBalanceSheet() {
        return ResponseEntity.ok(reportService.getBalanceSheet());
    }

    @GetMapping({"/profit-and-loss", "/profit-loss"})
    public ResponseEntity<ProfitAndLossDto> getProfitAndLoss() {
        return ResponseEntity.ok(reportService.getProfitAndLoss());
    }

    @GetMapping({"/budget-report", "/budget"})
    public ResponseEntity<List<BudgetReportDto>> getBudgetReport() {
        return ResponseEntity.ok(reportService.getBudgetReport());
    }

    @GetMapping("/license-utilization")
    public ResponseEntity<List<LicenseUtilizationDto>> getLicenseUtilizationReport() {
        return ResponseEntity.ok(reportService.getLicenseUtilizationReport());
    }

    @GetMapping("/patron-activity")
    public ResponseEntity<List<PatronActivityDto>> getPatronActivityReport() {
        return ResponseEntity.ok(reportService.getPatronActivityReport());
    }
}
