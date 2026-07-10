package com.example.assetserviceticket.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateTicketCommentRequest(
        @NotNull(message = "User id must not be null")
        Long userId,

        @NotBlank(message = "Comment content must not be blank")
        String content
) {
}
