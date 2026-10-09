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
import com.example.assetserviceticket.repository.TicketCommentRepository;
import com.example.assetserviceticket.repository.UserRepository;
import com.example.assetserviceticket.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TicketServiceImpl implements TicketService {

    private final ServiceTicketRepository ticketRepository;
    private final AssetRepository assetRepository;
    private final UserRepository userRepository;
    private final TicketCommentRepository commentRepository;

    @Override
    @Transactional
    public TicketResponse create(CreateTicketRequest request, String creatorEmail) {
        User creator = getUserByEmail(creatorEmail);
        Asset asset = getAsset(request.assetId());
        if (creator.getRole() == UserRole.EMPLOYEE
                && (asset.getAssignedUser() == null || !asset.getAssignedUser().getId().equals(creator.getId()))) {
            throw new ResourceNotFoundException("Bu demirbaş bulunamadı veya size zimmetli değil.");
        }
        ServiceTicket ticket = new ServiceTicket();
        ticket.setTicketNumber(generateTicketNumber());
        ticket.setTitle(request.title());
        ticket.setDescription(request.description());
        ticket.setPriority(request.priority());
        ticket.setStatus(TicketStatus.OPEN);
        ticket.setAsset(asset);
        ticket.setCreatedBy(creator);
        return toResponse(ticketRepository.save(ticket));
    }

    @Override
    public List<TicketResponse> findAll(String userEmail) {
        User currentUser = getUserByEmail(userEmail);
        List<ServiceTicket> tickets = switch (currentUser.getRole()) {
            case ADMIN -> ticketRepository.findAll();
            case TECHNICIAN -> ticketRepository.findByAssignedTechnicianIdOrderByCreatedAtDesc(currentUser.getId());
            case EMPLOYEE -> ticketRepository.findByCreatedByIdOrderByCreatedAtDesc(currentUser.getId());
        };
        return tickets.stream().map(this::toResponse).toList();
    }

    @Override
    public TicketResponse findById(Long id, String userEmail) {
        return toResponse(getTicketForUser(id, userEmail));
    }

    @Override
    @Transactional
    public TicketResponse update(Long id, UpdateTicketRequest request, String userEmail) {
        ServiceTicket ticket = getTicketForUser(id, userEmail);
        ensureEditable(ticket);
        ticket.setTitle(request.title());
        ticket.setDescription(request.description());
        ticket.setPriority(request.priority());
        return toResponse(ticketRepository.save(ticket));
    }

    @Override
    @Transactional
    public TicketResponse assign(Long id, AssignTicketRequest request, String userEmail) {
        User actor = getUserByEmail(userEmail);
        ServiceTicket ticket = getTicketForUser(id, userEmail);
        ensureEditable(ticket);
        if (request.technicianId() == null) {
            if (actor.getRole() != UserRole.ADMIN) {
                throw new BusinessRuleException("Teknisyen atamasını yalnızca yönetici kaldırabilir.");
            }
            ticket.setAssignedTechnician(null);
            ticket.setStatus(TicketStatus.OPEN);
            return toResponse(ticketRepository.save(ticket));
        }
        User technician = getUser(request.technicianId());
        if (technician.getRole() != UserRole.TECHNICIAN) {
            throw new BusinessRuleException("Destek talebi yalnızca TEKNİSYEN rolündeki bir kullanıcıya atanabilir.");
        }
        if (!technician.isActive()) {
            throw new BusinessRuleException("Devre dışı bırakılmış kullanıcılar teknisyen olarak atanamaz.");
        }
        if (actor.getRole() == UserRole.TECHNICIAN && !technician.getId().equals(actor.getId())) {
            throw new BusinessRuleException("Teknisyen yalnızca kendisine atanmış talepte işlem yapabilir.");
        }
        ticket.setAssignedTechnician(technician);
        ticket.setStatus(TicketStatus.ASSIGNED);
        return toResponse(ticketRepository.save(ticket));
    }

    @Override
    @Transactional
    public TicketResponse updateStatus(Long id, UpdateTicketStatusRequest request, String userEmail) {
        ServiceTicket ticket = getTicketForUser(id, userEmail);
        ensureEditable(ticket);
        ticket.setStatus(request.status());
        if (request.status() == TicketStatus.CLOSED) {
            ticket.setClosedAt(LocalDateTime.now());
        } else {
            ticket.setClosedAt(null);
        }
        return toResponse(ticketRepository.save(ticket));
    }

    @Override
    @Transactional
    public void delete(Long id, String userEmail) {
        ServiceTicket ticket = getTicketForUser(id, userEmail);
        ensureEditable(ticket);
        if (commentRepository.existsByTicketId(id)) {
            throw new BusinessRuleException("Yorumları bulunan destek talebi silinemez.");
        }
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
            throw new BusinessRuleException("Kapatılmış veya iptal edilmiş destek talepleri güncellenemez.");
        }
    }

    private ServiceTicket getTicket(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bu numaraya sahip destek talebi bulunamadı. Numara: " + id));
    }

    private ServiceTicket getTicketForUser(Long id, String email) {
        ServiceTicket ticket = getTicket(id);
        User actor = getUserByEmail(email);
        boolean allowed = switch (actor.getRole()) {
            case ADMIN -> true;
            case TECHNICIAN -> ticket.getAssignedTechnician() != null
                    && ticket.getAssignedTechnician().getId().equals(actor.getId());
            case EMPLOYEE -> ticket.getCreatedBy().getId().equals(actor.getId());
        };
        if (!allowed) {
            throw new ResourceNotFoundException("Bu destek talebi bulunamadı.");
        }
        return ticket;
    }

    private Asset getAsset(Long id) {
        return assetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bu numaraya sahip demirbaş bulunamadı. Numara: " + id));
    }

    private User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bu numaraya sahip kullanıcı bulunamadı. Numara: " + id));
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("Oturum kullanıcısı bulunamadı."));
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
