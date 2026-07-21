package com.example.assetserviceticket.service.impl;

import com.example.assetserviceticket.dto.request.CreateAssetRequest;
import com.example.assetserviceticket.dto.request.UpdateAssetRequest;
import com.example.assetserviceticket.dto.response.AssetResponse;
import com.example.assetserviceticket.entity.Asset;
import com.example.assetserviceticket.entity.Department;
import com.example.assetserviceticket.entity.User;
import com.example.assetserviceticket.enums.AssetStatus;
import com.example.assetserviceticket.enums.AssetType;
import com.example.assetserviceticket.exception.BusinessRuleException;
import com.example.assetserviceticket.exception.DuplicateResourceException;
import com.example.assetserviceticket.exception.ResourceNotFoundException;
import com.example.assetserviceticket.repository.AssetRepository;
import com.example.assetserviceticket.repository.DepartmentRepository;
import com.example.assetserviceticket.repository.ServiceTicketRepository;
import com.example.assetserviceticket.repository.UserRepository;
import com.example.assetserviceticket.service.AssetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AssetServiceImpl implements AssetService {

    private final AssetRepository assetRepository;
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;
    private final ServiceTicketRepository ticketRepository;

    @Override
    @Transactional
    public AssetResponse create(CreateAssetRequest request) {
        if (assetRepository.existsByAssetTagIgnoreCase(request.assetTag())) {
            throw new DuplicateResourceException("Asset already exists with tag: " + request.assetTag());
        }

        Asset asset = new Asset();
        fillAsset(asset, request.assetTag(), request.name(), request.type(), request.status(), request.serialNumber(),
                request.brand(), request.model(), request.departmentId(), request.assignedUserId());
        return toResponse(assetRepository.save(asset));
    }

    @Override
    public List<AssetResponse> findAll(AssetType type, AssetStatus status) {
        List<Asset> assets;
        if (type != null && status != null) {
            assets = assetRepository.findByTypeAndStatus(type, status);
        } else if (type != null) {
            assets = assetRepository.findByType(type);
        } else if (status != null) {
            assets = assetRepository.findByStatus(status);
        } else {
            assets = assetRepository.findAll();
        }
        return assets.stream().map(this::toResponse).toList();
    }

    @Override
    public AssetResponse findById(Long id) {
        return toResponse(getAsset(id));
    }

    @Override
    public AssetResponse findByAssetTag(String assetTag) {
        return toResponse(assetRepository.findByAssetTagIgnoreCase(assetTag)
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found with tag: " + assetTag)));
    }

    @Override
    @Transactional
    public AssetResponse update(Long id, UpdateAssetRequest request) {
        Asset asset = getAsset(id);
        if (!asset.getAssetTag().equalsIgnoreCase(request.assetTag())
                && assetRepository.existsByAssetTagIgnoreCase(request.assetTag())) {
            throw new DuplicateResourceException("Asset already exists with tag: " + request.assetTag());
        }

        fillAsset(asset, request.assetTag(), request.name(), request.type(), request.status(), request.serialNumber(),
                request.brand(), request.model(), request.departmentId(), request.assignedUserId());
        return toResponse(assetRepository.save(asset));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Asset asset = getAsset(id);
        if (ticketRepository.existsByAssetId(id)) {
            throw new BusinessRuleException("Asset cannot be deleted while service tickets are linked to it");
        }
        assetRepository.delete(asset);
    }

    private Asset getAsset(Long id) {
        return assetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found with id: " + id));
    }

    private void fillAsset(
            Asset asset,
            String assetTag,
            String name,
            AssetType type,
            AssetStatus status,
            String serialNumber,
            String brand,
            String model,
            Long departmentId,
            Long assignedUserId
    ) {
        asset.setAssetTag(assetTag);
        asset.setName(name);
        asset.setType(type);
        asset.setStatus(status);
        asset.setSerialNumber(serialNumber);
        asset.setBrand(brand);
        asset.setModel(model);
        asset.setDepartment(findDepartmentOrNull(departmentId));
        asset.setAssignedUser(findUserOrNull(assignedUserId));
    }

    private Department findDepartmentOrNull(Long departmentId) {
        if (departmentId == null) {
            return null;
        }
        return departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + departmentId));
    }

    private User findUserOrNull(Long userId) {
        if (userId == null) {
            return null;
        }
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }

    private AssetResponse toResponse(Asset asset) {
        Department department = asset.getDepartment();
        User assignedUser = asset.getAssignedUser();
        return new AssetResponse(
                asset.getId(),
                asset.getAssetTag(),
                asset.getName(),
                asset.getType(),
                asset.getStatus(),
                asset.getSerialNumber(),
                asset.getBrand(),
                asset.getModel(),
                department == null ? null : department.getId(),
                department == null ? null : department.getName(),
                assignedUser == null ? null : assignedUser.getId(),
                assignedUser == null ? null : assignedUser.getFullName(),
                asset.getCreatedAt()
        );
    }
}
