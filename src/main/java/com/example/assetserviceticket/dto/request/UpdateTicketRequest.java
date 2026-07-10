package com.example.assetserviceticket.dto.request;

import com.example.assetserviceticket.enums.TicketPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateTicketRequest(
        @NotBlank(message = "Title must not be blank")
        String title,

        @NotBlank(message = "Description must not be blank")
        String description,

        @NotNull(message = "Priority must not be null")
        TicketPriority priority
) {
}
