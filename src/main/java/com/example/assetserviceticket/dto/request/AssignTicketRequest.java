package com.example.assetserviceticket.dto.request;

import jakarta.validation.constraints.NotNull;

public record AssignTicketRequest(
        @NotNull(message = "Technician id must not be null")
        Long technicianId
) {
}
