package com.example.assetserviceticket.dto.request;

import com.example.assetserviceticket.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateUserRequest(
        @NotBlank(message = "Ad soyad boş bırakılamaz.")
        String fullName,

        @NotBlank(message = "E-posta adresi boş bırakılamaz.")
        @Email(message = "Geçerli bir e-posta adresi girilmelidir.")
        String email,

        @NotNull(message = "Kullanıcı rolü seçilmelidir.")
        UserRole role,

        Long departmentId,

        Boolean active
) {
}
