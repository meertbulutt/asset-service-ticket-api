package com.example.assetserviceticket.dto.request;

import com.example.assetserviceticket.enums.TicketStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Bir destek talebinin durumunu değiştirme isteği.")
public record UpdateTicketStatusRequest(
        @Schema(description = "Yeni talep durumu", example = "IN_PROGRESS")
        @NotNull(message = "Durum seçilmelidir.")
        TicketStatus status
) {
}
