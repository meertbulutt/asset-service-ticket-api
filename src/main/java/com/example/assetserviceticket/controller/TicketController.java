package com.example.assetserviceticket.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import com.example.assetserviceticket.dto.request.AssignTicketRequest;
import com.example.assetserviceticket.dto.request.CreateTicketRequest;
import com.example.assetserviceticket.dto.request.UpdateTicketRequest;
import com.example.assetserviceticket.dto.request.UpdateTicketStatusRequest;
import com.example.assetserviceticket.dto.response.TicketResponse;
import com.example.assetserviceticket.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Destek Talepleri", description = "Teknik destek talebi oluşturma, atama, güncelleme ve silme işlemleri.")
@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @PostMapping
    @Operation(summary = "Destek talebi oluştur", description = "Var olan demirbaşa yeni talep açar. Talep sahibi oturum açmış kullanıcıdan belirlenir.")
    @ResponseStatus(HttpStatus.CREATED)
    public TicketResponse create(@Valid @RequestBody CreateTicketRequest request, Authentication authentication) {
        return ticketService.create(request, authentication.getName());
    }

    @GetMapping
    @Operation(summary = "Destek taleplerini listele", description = "Kayıtlı tüm destek taleplerini envanter ve kullanıcı bilgileriyle döndürür.")
    public List<TicketResponse> findAll(Authentication authentication) {
        return ticketService.findAll(authentication.getName());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Destek talebi detayını getir", description = "Verilen ID numarasına sahip talebin ayrıntılarını döndürür.")
    public TicketResponse findById(@PathVariable Long id, Authentication authentication) {
        return ticketService.findById(id, authentication.getName());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Destek talebini güncelle", description = "Açık talebin başlığını, açıklamasını ve önceliğini günceller.")
    public TicketResponse update(@PathVariable Long id, @Valid @RequestBody UpdateTicketRequest request, Authentication authentication) {
        return ticketService.update(id, request, authentication.getName());
    }

    @PatchMapping("/{id}/assign")
    @Operation(summary = "Talebi teknisyene ata", description = "Aktif bir teknisyeni talebe atar ve talep durumunu Atandı yapar.")
    public TicketResponse assign(@PathVariable Long id, @Valid @RequestBody AssignTicketRequest request, Authentication authentication) {
        return ticketService.assign(id, request, authentication.getName());
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Talep durumunu güncelle", description = "Talep durumunu değiştirir; Kapalı seçildiğinde kapanış zamanını kaydeder.")
    public TicketResponse updateStatus(@PathVariable Long id, @Valid @RequestBody UpdateTicketStatusRequest request, Authentication authentication) {
        return ticketService.updateStatus(id, request, authentication.getName());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Destek talebini sil", description = "Yorumu bulunmayan ve kapatılmamış talebi siler.")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id, Authentication authentication) {
        ticketService.delete(id, authentication.getName());
    }
}
