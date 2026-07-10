package com.example.assetserviceticket.dto.response;

import java.time.LocalDateTime;

public record TicketCommentResponse(
        Long id,
        Long ticketId,
        Long userId,
        String userFullName,
        String content,
        LocalDateTime createdAt
) {
}
