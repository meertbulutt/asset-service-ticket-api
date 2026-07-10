package com.example.assetserviceticket.dto.response;

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
