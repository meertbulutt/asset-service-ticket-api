package com.example.assetserviceticket.dto.response;

import java.time.LocalDateTime;
import java.util.Map;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "API hatalarında istemciye Türkçe olarak döndürülen standart yanıt.")
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path,
        Map<String, String> fieldErrors
) {
}
