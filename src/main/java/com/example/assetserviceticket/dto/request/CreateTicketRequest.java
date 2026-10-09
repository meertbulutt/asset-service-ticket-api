package com.example.assetserviceticket.dto.request;

import com.example.assetserviceticket.enums.TicketPriority;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Yeni destek talebi açmak için gönderilen bilgiler. Talep numarası ve oluşturan kullanıcı oturumdan belirlenir.")
public record CreateTicketRequest(
        @Schema(description = "Sorunu kısa biçimde anlatan başlık", example = "Bilgisayar açılmıyor")
        @NotBlank(message = "Başlık boş bırakılamaz.")
        String title,

        @Schema(description = "Sorunun ayrıntılı açıklaması", example = "Güç düğmesine basıldığında cihaz tepki vermiyor.")
        @NotBlank(message = "Açıklama boş bırakılamaz.")
        String description,

        @Schema(description = "Talebin öncelik seviyesi", example = "HIGH")
        @NotNull(message = "Öncelik seçilmelidir.")
        TicketPriority priority,

        @Schema(description = "İlgili demirbaşın önceden listelenmiş ID numarası", example = "1")
        @NotNull(message = "Demirbaş numarası zorunludur.")
        Long assetId
) {
}
