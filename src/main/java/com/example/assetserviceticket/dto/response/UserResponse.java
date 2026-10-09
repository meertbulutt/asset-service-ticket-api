package com.example.assetserviceticket.dto.response;

import com.example.assetserviceticket.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Kullanıcı kimliği, rolü, departmanı ve hesap durumunu içeren yanıt.")
public record UserResponse(
        Long id,
        String fullName,
        String email,
        UserRole role,
        Long departmentId,
        String departmentName,
        boolean active,
        LocalDateTime createdAt
) {
}
