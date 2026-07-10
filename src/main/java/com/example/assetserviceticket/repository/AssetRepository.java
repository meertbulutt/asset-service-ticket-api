package com.example.assetserviceticket.repository;

import com.example.assetserviceticket.entity.Asset;
import com.example.assetserviceticket.enums.AssetStatus;
import com.example.assetserviceticket.enums.AssetType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AssetRepository extends JpaRepository<Asset, Long> {
    boolean existsByAssetTagIgnoreCase(String assetTag);

    Optional<Asset> findByAssetTagIgnoreCase(String assetTag);

    List<Asset> findByTypeAndStatus(AssetType type, AssetStatus status);

    List<Asset> findByType(AssetType type);

    List<Asset> findByStatus(AssetStatus status);

    long countByStatus(AssetStatus status);
}
