package com.example.assetserviceticket.dto.request;

import com.example.assetserviceticket.enums.AssetStatus;
import com.example.assetserviceticket.enums.AssetType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateAssetRequest(
        @NotBlank(message = "Demirbaş etiketi boş bırakılamaz.")
        String assetTag,

        @NotBlank(message = "Demirbaş adı boş bırakılamaz.")
        String name,

        @NotNull(message = "Demirbaş türü seçilmelidir.")
        AssetType type,

        @NotNull(message = "Demirbaş durumu seçilmelidir.")
        AssetStatus status,

        String serialNumber,
        String brand,
        String model,
        Long departmentId,
        Long assignedUserId
) {
}
