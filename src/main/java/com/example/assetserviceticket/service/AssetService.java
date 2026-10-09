package com.example.assetserviceticket.service;

import com.example.assetserviceticket.dto.request.CreateAssetRequest;
import com.example.assetserviceticket.dto.request.UpdateAssetRequest;
import com.example.assetserviceticket.dto.response.AssetResponse;
import com.example.assetserviceticket.enums.AssetStatus;
import com.example.assetserviceticket.enums.AssetType;

import java.util.List;

public interface AssetService {
    AssetResponse create(CreateAssetRequest request);

    List<AssetResponse> findAll(AssetType type, AssetStatus status, String userEmail);

    AssetResponse findById(Long id, String userEmail);

    AssetResponse findByAssetTag(String assetTag, String userEmail);

    AssetResponse update(Long id, UpdateAssetRequest request);

    void delete(Long id);
}
