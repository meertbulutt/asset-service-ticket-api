package com.example.assetserviceticket.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Demirbaş ve destek taleplerinin toplam ve durum bazlı sayılarını içeren özet rapor.")
public record ReportSummaryResponse(
        long totalAssets,
        long activeAssets,
        long inServiceAssets,
        long totalTickets,
        long openTickets,
        long inProgressTickets,
        long closedTickets,
        long criticalTickets
) {
}
