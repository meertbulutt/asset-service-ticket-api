package com.example.assetserviceticket.controller;

import com.example.assetserviceticket.dto.request.CreateAssetRequest;
import com.example.assetserviceticket.dto.request.UpdateAssetRequest;
import com.example.assetserviceticket.dto.response.AssetResponse;
import com.example.assetserviceticket.enums.AssetStatus;
import com.example.assetserviceticket.enums.AssetType;
import com.example.assetserviceticket.service.AssetService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/assets")
@RequiredArgsConstructor
public class AssetController {

    private final AssetService assetService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AssetResponse create(@Valid @RequestBody CreateAssetRequest request) {
        return assetService.create(request);
    }

    @GetMapping
    public List<AssetResponse> findAll(
            @RequestParam(required = false) AssetType type,
            @RequestParam(required = false) AssetStatus status
    ) {
        return assetService.findAll(type, status);
    }

    @GetMapping("/{id}")
    public AssetResponse findById(@PathVariable Long id) {
        return assetService.findById(id);
    }

    @GetMapping("/by-tag/{assetTag}")
    public AssetResponse findByAssetTag(@PathVariable String assetTag) {
        return assetService.findByAssetTag(assetTag);
    }

    @PutMapping("/{id}")
    public AssetResponse update(@PathVariable Long id, @Valid @RequestBody UpdateAssetRequest request) {
        return assetService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        assetService.delete(id);
    }
}
