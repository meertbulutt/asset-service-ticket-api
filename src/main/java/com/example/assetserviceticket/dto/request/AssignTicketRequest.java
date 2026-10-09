package com.example.assetserviceticket.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Bir destek talebini aktif bir teknisyene atama isteği.")
public record AssignTicketRequest(
        @Schema(description = "Teknisyen ID numarası; yalnızca yönetici isteğinde null gönderilirse atama kaldırılır", example = "2", nullable = true)
        Long technicianId
) {
}
