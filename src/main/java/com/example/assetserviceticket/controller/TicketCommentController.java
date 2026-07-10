package com.example.assetserviceticket.controller;

import com.example.assetserviceticket.dto.request.CreateTicketCommentRequest;
import com.example.assetserviceticket.dto.response.TicketCommentResponse;
import com.example.assetserviceticket.service.TicketCommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tickets/{ticketId}/comments")
@RequiredArgsConstructor
public class TicketCommentController {

    private final TicketCommentService commentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TicketCommentResponse create(
            @PathVariable Long ticketId,
            @Valid @RequestBody CreateTicketCommentRequest request
    ) {
        return commentService.create(ticketId, request);
    }

    @GetMapping
    public List<TicketCommentResponse> findByTicketId(@PathVariable Long ticketId) {
        return commentService.findByTicketId(ticketId);
    }
}
