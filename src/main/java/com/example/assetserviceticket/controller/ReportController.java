package com.example.assetserviceticket.controller;

import com.example.assetserviceticket.dto.response.ReportSummaryResponse;
import com.example.assetserviceticket.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/summary")
    public ReportSummaryResponse getSummary() {
        return reportService.getSummary();
    }
}
