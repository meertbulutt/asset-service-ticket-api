package com.example.assetserviceticket.dto.request;

import com.example.assetserviceticket.enums.TicketStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateTicketStatusRequest(
        @NotNull(message = "Status must not be null")
        TicketStatus status
) {
}
