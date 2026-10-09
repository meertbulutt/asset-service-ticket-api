package com.example.assetserviceticket.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import com.example.assetserviceticket.dto.response.ReportSummaryResponse;
import com.example.assetserviceticket.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Raporlar", description = "Demirbaş ve destek talepleri için özet raporlar.")
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/summary")
    @Operation(summary = "Rol kapsamındaki durum raporunu getir", description = "Oturum açmış kullanıcının rolüne ve erişebildiği kayıtlara göre demirbaş ve talep sayılarını döndürür.")
    public ReportSummaryResponse getSummary(Authentication authentication) {
        return reportService.getSummary(authentication.getName());
    }
}
