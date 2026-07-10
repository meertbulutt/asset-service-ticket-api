package com.example.assetserviceticket.dto.response;

import com.example.assetserviceticket.enums.UserRole;

import java.time.LocalDateTime;

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
