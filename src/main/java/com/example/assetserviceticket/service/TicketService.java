package com.example.assetserviceticket.service;

import com.example.assetserviceticket.dto.request.AssignTicketRequest;
import com.example.assetserviceticket.dto.request.CreateTicketRequest;
import com.example.assetserviceticket.dto.request.UpdateTicketRequest;
import com.example.assetserviceticket.dto.request.UpdateTicketStatusRequest;
import com.example.assetserviceticket.dto.response.TicketResponse;

import java.util.List;

public interface TicketService {
    TicketResponse create(CreateTicketRequest request, String creatorEmail);

    List<TicketResponse> findAll(String userEmail);

    TicketResponse findById(Long id, String userEmail);

    TicketResponse update(Long id, UpdateTicketRequest request, String userEmail);

    TicketResponse assign(Long id, AssignTicketRequest request, String userEmail);

    TicketResponse updateStatus(Long id, UpdateTicketStatusRequest request, String userEmail);

    void delete(Long id, String userEmail);
}
