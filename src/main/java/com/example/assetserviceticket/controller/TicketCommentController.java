package com.example.assetserviceticket.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import com.example.assetserviceticket.dto.request.CreateTicketCommentRequest;
import com.example.assetserviceticket.dto.response.TicketCommentResponse;
import com.example.assetserviceticket.service.TicketCommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Talep Yorumları", description = "Destek taleplerine yorum ekleme ve yorumları listeleme işlemleri.")
@RestController
@RequestMapping("/api/tickets/{ticketId}/comments")
@RequiredArgsConstructor
public class TicketCommentController {

    private final TicketCommentService commentService;

    @PostMapping
    @Operation(summary = "Talebe yorum ekle", description = "Belirtilen talebe, oturum açmış kullanıcı adına açıklama veya çözüm notu ekler.")
    @ResponseStatus(HttpStatus.CREATED)
    public TicketCommentResponse create(
            @PathVariable Long ticketId,
            @Valid @RequestBody CreateTicketCommentRequest request,
            Authentication authentication
    ) {
        return commentService.create(ticketId, request, authentication.getName());
    }

    @GetMapping
    @Operation(summary = "Talep yorumlarını listele", description = "Bir destek talebine eklenen yorumları oluşturulma sırasıyla döndürür.")
    public List<TicketCommentResponse> findByTicketId(@PathVariable Long ticketId, Authentication authentication) {
        return commentService.findByTicketId(ticketId, authentication.getName());
    }
}
