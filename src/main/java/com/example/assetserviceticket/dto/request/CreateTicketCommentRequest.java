package com.example.assetserviceticket.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Destek talebine yorum eklemek için gönderilen bilgiler.")
public record CreateTicketCommentRequest(
        @Schema(description = "Yorum veya çözüm notu", example = "Cihazın ağ bağlantısı kontrol ediliyor.")
        @NotBlank(message = "Yorum boş bırakılamaz.")
        String content
) {
}
