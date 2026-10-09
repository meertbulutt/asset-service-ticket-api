package com.example.assetserviceticket.dto.response;

import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Departman adı, açıklaması ve oluşturulma zamanını içeren yanıt.")
public record DepartmentResponse(
        Long id,
        String name,
        String description,
        LocalDateTime createdAt
) {
}
