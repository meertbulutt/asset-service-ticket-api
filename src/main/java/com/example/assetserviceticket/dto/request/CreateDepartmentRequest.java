package com.example.assetserviceticket.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateDepartmentRequest(
        @NotBlank(message = "Departman adı boş bırakılamaz.")
        @Size(max = 100, message = "Departman adı en fazla 100 karakter olabilir.")
        String name,

        @Size(max = 255, message = "Açıklama en fazla 255 karakter olabilir.")
        String description
) {
}
