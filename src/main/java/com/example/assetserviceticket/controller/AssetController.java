package com.example.assetserviceticket.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import com.example.assetserviceticket.dto.request.CreateAssetRequest;
import com.example.assetserviceticket.dto.request.UpdateAssetRequest;
import com.example.assetserviceticket.dto.response.AssetResponse;
import com.example.assetserviceticket.enums.AssetStatus;
import com.example.assetserviceticket.enums.AssetType;
import com.example.assetserviceticket.service.AssetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Demirbaşlar", description = "Demirbaş kayıtlarını oluşturma, arama, filtreleme, güncelleme ve silme işlemleri.")
@RestController
@RequestMapping("/api/assets")
@RequiredArgsConstructor
public class AssetController {

    private final AssetService assetService;

    @PostMapping
    @Operation(summary = "Demirbaş oluştur", description = "Yeni bir demirbaş kaydı ekler. ID veritabanı tarafından atanır.")
    @ResponseStatus(HttpStatus.CREATED)
    public AssetResponse create(@Valid @RequestBody CreateAssetRequest request) {
        return assetService.create(request);
    }

    @GetMapping
    @Operation(summary = "Demirbaşları listele", description = "Tüm demirbaşları listeler. Tür ve durum parametreleri isteğe bağlı filtrelerdir.")
    public List<AssetResponse> findAll(
            @RequestParam(required = false) AssetType type,
            @RequestParam(required = false) AssetStatus status,
            Authentication authentication
    ) {
        return assetService.findAll(type, status, authentication.getName());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Demirbaş detayını getir", description = "Verilen ID numarasına sahip demirbaşın ayrıntılarını döndürür.")
    public AssetResponse findById(@PathVariable Long id, Authentication authentication) {
        return assetService.findById(id, authentication.getName());
    }

    @GetMapping("/by-tag/{assetTag}")
    @Operation(summary = "Etiketle demirbaş ara", description = "Demirbaş etiket numarasını kullanarak ilgili kaydı bulur.")
    public AssetResponse findByAssetTag(@PathVariable String assetTag, Authentication authentication) {
        return assetService.findByAssetTag(assetTag, authentication.getName());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Demirbaş bilgilerini güncelle", description = "Belirtilen demirbaşın alanlarını gönderilen bilgilerle günceller.")
    public AssetResponse update(@PathVariable Long id, @Valid @RequestBody UpdateAssetRequest request) {
        return assetService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Demirbaş kaydını sil", description = "Destek talebiyle ilişkili olmayan demirbaş kaydını siler.")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        assetService.delete(id);
    }
}
