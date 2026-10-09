package com.example.assetserviceticket.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import com.example.assetserviceticket.dto.request.CreateDepartmentRequest;
import com.example.assetserviceticket.dto.request.UpdateDepartmentRequest;
import com.example.assetserviceticket.dto.response.DepartmentResponse;
import com.example.assetserviceticket.service.DepartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Departmanlar", description = "Departman kayıtlarını yönetme işlemleri.")
@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @PostMapping
    @Operation(summary = "Departman oluştur", description = "Yeni bir departman kaydı ekler.")
    @ResponseStatus(HttpStatus.CREATED)
    public DepartmentResponse create(@Valid @RequestBody CreateDepartmentRequest request) {
        return departmentService.create(request);
    }

    @GetMapping
    @Operation(summary = "Departmanları listele", description = "Kayıtlı tüm departmanları döndürür.")
    public List<DepartmentResponse> findAll() {
        return departmentService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Departman detayını getir", description = "Verilen ID numarasına sahip departmanı bulur.")
    public DepartmentResponse findById(@PathVariable Long id) {
        return departmentService.findById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Departmanı güncelle", description = "Departman adını ve açıklamasını günceller.")
    public DepartmentResponse update(@PathVariable Long id, @Valid @RequestBody UpdateDepartmentRequest request) {
        return departmentService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Departmanı sil", description = "Kullanıcı veya demirbaş bağlı olmayan departmanı siler.")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        departmentService.delete(id);
    }
}
