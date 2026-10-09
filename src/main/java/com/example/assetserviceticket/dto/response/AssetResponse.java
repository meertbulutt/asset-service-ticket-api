package com.example.assetserviceticket.dto.response;

import com.example.assetserviceticket.enums.AssetStatus;
import com.example.assetserviceticket.enums.AssetType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Demirbaş ayrıntılarını ve bağlı departman/kullanıcı bilgilerini içeren API yanıtı.")
public record AssetResponse(
        Long id,
        String assetTag,
        String name,
        AssetType type,
        AssetStatus status,
        String serialNumber,
        String brand,
        String model,
        Long departmentId,
        String departmentName,
        Long assignedUserId,
        String assignedUserName,
        LocalDateTime createdAt
) {
}
