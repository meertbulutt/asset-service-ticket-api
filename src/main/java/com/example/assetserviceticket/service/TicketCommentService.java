package com.example.assetserviceticket.service;

import com.example.assetserviceticket.dto.request.CreateTicketCommentRequest;
import com.example.assetserviceticket.dto.response.TicketCommentResponse;

import java.util.List;

public interface TicketCommentService {
    TicketCommentResponse create(Long ticketId, CreateTicketCommentRequest request);

    List<TicketCommentResponse> findByTicketId(Long ticketId);
}
