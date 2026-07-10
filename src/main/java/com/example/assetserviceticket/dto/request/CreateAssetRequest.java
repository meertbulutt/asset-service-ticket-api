package com.example.assetserviceticket.dto.request;

import com.example.assetserviceticket.enums.AssetStatus;
import com.example.assetserviceticket.enums.AssetType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateAssetRequest(
        @NotBlank(message = "Asset tag must not be blank")
        String assetTag,

        @NotBlank(message = "Asset name must not be blank")
        String name,

        @NotNull(message = "Asset type must not be null")
        AssetType type,

        @NotNull(message = "Asset status must not be null")
        AssetStatus status,

        String serialNumber,
        String brand,
        String model,
        Long departmentId,
        Long assignedUserId
) {
}
