package com.example.assetserviceticket.service;

import com.example.assetserviceticket.dto.request.AssignTicketRequest;
import com.example.assetserviceticket.dto.request.CreateTicketRequest;
import com.example.assetserviceticket.dto.request.UpdateTicketRequest;
import com.example.assetserviceticket.dto.request.UpdateTicketStatusRequest;
import com.example.assetserviceticket.dto.response.TicketResponse;

import java.util.List;

public interface TicketService {
    TicketResponse create(CreateTicketRequest request);

    List<TicketResponse> findAll();

    TicketResponse findById(Long id);

    TicketResponse update(Long id, UpdateTicketRequest request);

    TicketResponse assign(Long id, AssignTicketRequest request);

    TicketResponse updateStatus(Long id, UpdateTicketStatusRequest request);

    void delete(Long id);
}
