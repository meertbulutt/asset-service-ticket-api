package com.example.assetserviceticket.dto.request;

import com.example.assetserviceticket.enums.AssetStatus;
import com.example.assetserviceticket.enums.AssetType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Yeni demirbaş oluşturmak için gönderilen bilgiler. ID alanı gönderilmez; veritabanı tarafından atanır.")
public record CreateAssetRequest(
        @Schema(description = "Demirbaşı tanımlayan benzersiz envanter etiketi", example = "AST-0009")
        @NotBlank(message = "Demirbaş etiketi boş bırakılamaz.")
        String assetTag,

        @Schema(description = "Demirbaşın görünen adı", example = "Lenovo ThinkPad Dizüstü Bilgisayar")
        @NotBlank(message = "Demirbaş adı boş bırakılamaz.")
        String name,

        @Schema(description = "Demirbaş türü", example = "LAPTOP")
        @NotNull(message = "Demirbaş türü seçilmelidir.")
        AssetType type,

        @Schema(description = "Demirbaşın kullanım durumu", example = "ACTIVE")
        @NotNull(message = "Demirbaş durumu seçilmelidir.")
        AssetStatus status,

        @Schema(description = "Varsa üretici seri numarası", example = "SN-2026-001")
        String serialNumber,
        @Schema(description = "Üretici marka", example = "Lenovo")
        String brand,
        @Schema(description = "Ürün modeli", example = "ThinkPad E14")
        String model,
        @Schema(description = "İsteğe bağlı departman ID numarası", example = "1")
        Long departmentId,
        @Schema(description = "İsteğe bağlı zimmetli kullanıcı ID numarası", example = "3")
        Long assignedUserId
) {
}
