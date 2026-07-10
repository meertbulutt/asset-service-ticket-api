package com.example.assetserviceticket.dto.request;

import com.example.assetserviceticket.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateUserRequest(
        @NotBlank(message = "Full name must not be blank")
        String fullName,

        @NotBlank(message = "Email must not be blank")
        @Email(message = "Email must be valid")
        String email,

        @NotNull(message = "Role must not be null")
        UserRole role,

        Long departmentId
) {
}
