package com.example.assetserviceticket.dto.request;

import com.example.assetserviceticket.enums.TicketPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateTicketRequest(
        @NotBlank(message = "Başlık boş bırakılamaz.")
        String title,

        @NotBlank(message = "Açıklama boş bırakılamaz.")
        String description,

        @NotNull(message = "Öncelik seçilmelidir.")
        TicketPriority priority
) {
}
