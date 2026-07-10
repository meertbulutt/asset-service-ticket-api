package com.example.assetserviceticket.service.impl;

import com.example.assetserviceticket.dto.request.AssignTicketRequest;
import com.example.assetserviceticket.dto.request.CreateTicketRequest;
import com.example.assetserviceticket.dto.request.UpdateTicketRequest;
import com.example.assetserviceticket.dto.request.UpdateTicketStatusRequest;
import com.example.assetserviceticket.dto.response.TicketResponse;
import com.example.assetserviceticket.entity.Asset;
import com.example.assetserviceticket.entity.ServiceTicket;
import com.example.assetserviceticket.entity.User;
import com.example.assetserviceticket.enums.TicketStatus;
import com.example.assetserviceticket.enums.UserRole;
import com.example.assetserviceticket.exception.BusinessRuleException;
import com.example.assetserviceticket.exception.ResourceNotFoundException;
import com.example.assetserviceticket.repository.AssetRepository;
import com.example.assetserviceticket.repository.ServiceTicketRepository;
import com.example.assetserviceticket.repository.UserRepository;
import com.example.assetserviceticket.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final ServiceTicketRepository ticketRepository;
    private final AssetRepository assetRepository;
    private final UserRepository userRepository;

    @Override
    public TicketResponse create(CreateTicketRequest request) {
        ServiceTicket ticket = new ServiceTicket();
        ticket.setTicketNumber(generateTicketNumber());
        ticket.setTitle(request.title());
        ticket.setDescription(request.description());
        ticket.setPriority(request.priority());
        ticket.setStatus(TicketStatus.OPEN);
        ticket.setAsset(getAsset(request.assetId()));
        ticket.setCreatedBy(getUser(request.createdById()));
        return toResponse(ticketRepository.save(ticket));
    }

    @Override
    public List<TicketResponse> findAll() {
        return ticketRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public TicketResponse findById(Long id) {
        return toResponse(getTicket(id));
    }

    @Override
    public TicketResponse update(Long id, UpdateTicketRequest request) {
        ServiceTicket ticket = getTicket(id);
        ensureEditable(ticket);
        ticket.setTitle(request.title());
        ticket.setDescription(request.description());
        ticket.setPriority(request.priority());
        return toResponse(ticketRepository.save(ticket));
    }

    @Override
    public TicketResponse assign(Long id, AssignTicketRequest request) {
        ServiceTicket ticket = getTicket(id);
        ensureEditable(ticket);
        User technician = getUser(request.technicianId());
        if (technician.getRole() != UserRole.TECHNICIAN) {
            throw new BusinessRuleException("Assigned user must have TECHNICIAN role");
        }
        ticket.setAssignedTechnician(technician);
        ticket.setStatus(TicketStatus.ASSIGNED);
        return toResponse(ticketRepository.save(ticket));
    }

    @Override
    public TicketResponse updateStatus(Long id, UpdateTicketStatusRequest request) {
        ServiceTicket ticket = getTicket(id);
        ensureEditable(ticket);
        ticket.setStatus(request.status());
        if (request.status() == TicketStatus.CLOSED) {
            ticket.setClosedAt(LocalDateTime.now());
        }
        return toResponse(ticketRepository.save(ticket));
    }

    @Override
    public void delete(Long id) {
        ServiceTicket ticket = getTicket(id);
        ensureEditable(ticket);
        ticketRepository.delete(ticket);
    }

    private String generateTicketNumber() {
        long nextNumber = ticketRepository.findTopByOrderByIdDesc()
                .map(ticket -> ticket.getId() + 1)
                .orElse(1L);
        return "TCK-%06d".formatted(nextNumber);
    }

    private void ensureEditable(ServiceTicket ticket) {
        if (ticket.getStatus() == TicketStatus.CLOSED || ticket.getStatus() == TicketStatus.CANCELLED) {
            throw new BusinessRuleException("Closed or cancelled tickets cannot be updated");
        }
    }

    private ServiceTicket getTicket(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + id));
    }

    private Asset getAsset(Long id) {
        return assetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found with id: " + id));
    }

    private User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    private TicketResponse toResponse(ServiceTicket ticket) {
        User assignedTechnician = ticket.getAssignedTechnician();
        return new TicketResponse(
                ticket.getId(),
                ticket.getTicketNumber(),
                ticket.getTitle(),
                ticket.getDescription(),
                ticket.getStatus(),
                ticket.getPriority(),
                ticket.getAsset().getId(),
                ticket.getAsset().getName(),
                ticket.getCreatedBy().getId(),
                ticket.getCreatedBy().getFullName(),
                assignedTechnician == null ? null : assignedTechnician.getId(),
                assignedTechnician == null ? null : assignedTechnician.getFullName(),
                ticket.getCreatedAt(),
                ticket.getUpdatedAt(),
                ticket.getClosedAt()
        );
    }
}
