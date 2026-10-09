package com.example.assetserviceticket.dto.request;

import com.example.assetserviceticket.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Yeni kullanıcı oluşturmak için gerekli bilgiler. Kullanıcı numarası sistem tarafından oluşturulur.")
public record CreateUserRequest(
        @Schema(description = "Kullanıcının adı ve soyadı", example = "Deniz Yılmaz")
        @NotBlank(message = "Ad soyad boş bırakılamaz.")
        String fullName,

        @NotBlank(message = "E-posta adresi boş bırakılamaz.")
        @Email(message = "Geçerli bir e-posta adresi girilmelidir.")
        @Schema(description = "Benzersiz e-posta adresi", example = "deniz.yilmaz@kurum.com")
        String email,

        @Schema(description = "En az 8 karakterli giriş parolası", example = "GucluParola123!")
        @NotBlank(message = "Parola boş bırakılamaz.")
        @Size(min = 8, max = 72, message = "Parola 8 ile 72 karakter arasında olmalıdır.")
        String password,

        @Schema(description = "Kullanıcının sistemdeki rolü", example = "EMPLOYEE")
        @NotNull(message = "Kullanıcı rolü seçilmelidir.")
        UserRole role,

        @Schema(description = "Kullanıcının bağlı olduğu departmanın ID numarası; isteğe bağlıdır", example = "2")
        Long departmentId
) {
}
