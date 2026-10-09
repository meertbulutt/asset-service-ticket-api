package com.example.assetserviceticket.dto.response;

import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Destek talebi yorumunun yazarı, metni ve oluşturulma zamanını içeren yanıt.")
public record TicketCommentResponse(
        Long id,
        Long ticketId,
        Long userId,
        String userFullName,
        String content,
        LocalDateTime createdAt
) {
}
