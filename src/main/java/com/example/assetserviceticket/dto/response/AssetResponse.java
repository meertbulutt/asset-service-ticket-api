package com.example.assetserviceticket.dto.response;

import com.example.assetserviceticket.enums.AssetStatus;
import com.example.assetserviceticket.enums.AssetType;

import java.time.LocalDateTime;

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
