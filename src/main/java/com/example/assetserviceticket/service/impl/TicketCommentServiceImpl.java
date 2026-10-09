package com.example.assetserviceticket.service.impl;

import com.example.assetserviceticket.dto.request.CreateTicketCommentRequest;
import com.example.assetserviceticket.dto.response.TicketCommentResponse;
import com.example.assetserviceticket.entity.ServiceTicket;
import com.example.assetserviceticket.entity.TicketComment;
import com.example.assetserviceticket.entity.User;
import com.example.assetserviceticket.enums.TicketStatus;
import com.example.assetserviceticket.exception.BusinessRuleException;
import com.example.assetserviceticket.exception.ResourceNotFoundException;
import com.example.assetserviceticket.repository.ServiceTicketRepository;
import com.example.assetserviceticket.repository.TicketCommentRepository;
import com.example.assetserviceticket.repository.UserRepository;
import com.example.assetserviceticket.service.TicketCommentService;
import com.example.assetserviceticket.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TicketCommentServiceImpl implements TicketCommentService {

    private final TicketCommentRepository commentRepository;
    private final ServiceTicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final TicketService ticketService;

    @Override
    @Transactional
    public TicketCommentResponse create(Long ticketId, CreateTicketCommentRequest request, String authorEmail) {
        var ticket = ticketService.findById(ticketId, authorEmail);
        if (ticket.status() == TicketStatus.CLOSED || ticket.status() == TicketStatus.CANCELLED) {
            throw new BusinessRuleException("Kapatılmış veya iptal edilmiş talebe yeni yorum eklenemez.");
        }
        TicketComment comment = new TicketComment();
        comment.setTicket(getTicket(ticketId));
        comment.setUser(getUserByEmail(authorEmail));
        comment.setContent(request.content());
        return toResponse(commentRepository.save(comment));
    }

    @Override
    public List<TicketCommentResponse> findByTicketId(Long ticketId, String userEmail) {
        ticketService.findById(ticketId, userEmail);
        if (!ticketRepository.existsById(ticketId)) {
            throw new ResourceNotFoundException("Bu numaraya sahip destek talebi bulunamadı. Numara: " + ticketId);
        }
        return commentRepository.findByTicketIdOrderByCreatedAtAsc(ticketId).stream()
                .map(this::toResponse)
                .toList();
    }

    private ServiceTicket getTicket(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bu numaraya sahip destek talebi bulunamadı. Numara: " + id));
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("Oturum kullanıcısı bulunamadı."));
    }

    private TicketCommentResponse toResponse(TicketComment comment) {
        return new TicketCommentResponse(
                comment.getId(),
                comment.getTicket().getId(),
                comment.getUser().getId(),
                comment.getUser().getFullName(),
                comment.getContent(),
                comment.getCreatedAt()
        );
    }
}
