package com.example.assetserviceticket.dto.response;

import com.example.assetserviceticket.enums.TicketPriority;
import com.example.assetserviceticket.enums.TicketStatus;

import java.time.LocalDateTime;

public record TicketResponse(
        Long id,
        String ticketNumber,
        String title,
        String description,
        TicketStatus status,
        TicketPriority priority,
        Long assetId,
        String assetName,
        Long createdById,
        String createdByName,
        Long assignedTechnicianId,
        String assignedTechnicianName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime closedAt
) {
}
