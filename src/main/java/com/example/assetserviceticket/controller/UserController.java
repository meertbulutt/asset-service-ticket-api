package com.example.assetserviceticket.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import com.example.assetserviceticket.dto.request.CreateUserRequest;
import com.example.assetserviceticket.dto.request.UpdateUserRequest;
import com.example.assetserviceticket.dto.response.UserResponse;
import com.example.assetserviceticket.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Kullanıcılar", description = "Kullanıcı kayıtlarını yönetme ve kullanıcıları devre dışı bırakma işlemleri.")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    @Operation(summary = "Kullanıcı oluştur", description = "Yeni bir kullanıcı kaydı ekler. E-posta adresi benzersiz olmalıdır.")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse create(@Valid @RequestBody CreateUserRequest request) {
        return userService.create(request);
    }

    @GetMapping
    @Operation(summary = "Kullanıcıları listele", description = "Kayıtlı kullanıcıları ve rollerini döndürür.")
    public List<UserResponse> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Kullanıcı detayını getir", description = "Verilen ID numarasına sahip kullanıcının bilgilerini bulur.")
    public UserResponse findById(@PathVariable Long id) {
        return userService.findById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Kullanıcı bilgilerini güncelle", description = "Kullanıcı adı, e-posta, rol ve departman bilgilerini günceller.")
    public UserResponse update(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest request) {
        return userService.update(id, request);
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(summary = "Kullanıcıyı devre dışı bırak", description = "Kullanıcı kaydını silmeden hesabı pasif duruma getirir.")
    public UserResponse deactivate(@PathVariable Long id) {
        return userService.deactivate(id);
    }
}
