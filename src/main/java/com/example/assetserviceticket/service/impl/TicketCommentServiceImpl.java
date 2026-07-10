package com.example.assetserviceticket.service.impl;

import com.example.assetserviceticket.dto.request.CreateTicketCommentRequest;
import com.example.assetserviceticket.dto.response.TicketCommentResponse;
import com.example.assetserviceticket.entity.ServiceTicket;
import com.example.assetserviceticket.entity.TicketComment;
import com.example.assetserviceticket.entity.User;
import com.example.assetserviceticket.exception.ResourceNotFoundException;
import com.example.assetserviceticket.repository.ServiceTicketRepository;
import com.example.assetserviceticket.repository.TicketCommentRepository;
import com.example.assetserviceticket.repository.UserRepository;
import com.example.assetserviceticket.service.TicketCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketCommentServiceImpl implements TicketCommentService {

    private final TicketCommentRepository commentRepository;
    private final ServiceTicketRepository ticketRepository;
    private final UserRepository userRepository;

    @Override
    public TicketCommentResponse create(Long ticketId, CreateTicketCommentRequest request) {
        TicketComment comment = new TicketComment();
        comment.setTicket(getTicket(ticketId));
        comment.setUser(getUser(request.userId()));
        comment.setContent(request.content());
        return toResponse(commentRepository.save(comment));
    }

    @Override
    public List<TicketCommentResponse> findByTicketId(Long ticketId) {
        if (!ticketRepository.existsById(ticketId)) {
            throw new ResourceNotFoundException("Ticket not found with id: " + ticketId);
        }
        return commentRepository.findByTicketIdOrderByCreatedAtAsc(ticketId).stream()
                .map(this::toResponse)
                .toList();
    }

    private ServiceTicket getTicket(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + id));
    }

    private User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
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
