package com.example.assetserviceticket.service.impl;

import com.example.assetserviceticket.dto.request.CreateAssetRequest;
import com.example.assetserviceticket.dto.request.UpdateAssetRequest;
import com.example.assetserviceticket.dto.response.AssetResponse;
import com.example.assetserviceticket.entity.Asset;
import com.example.assetserviceticket.entity.Department;
import com.example.assetserviceticket.entity.User;
import com.example.assetserviceticket.enums.AssetStatus;
import com.example.assetserviceticket.enums.AssetType;
import com.example.assetserviceticket.enums.UserRole;
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
            throw new DuplicateResourceException("Bu demirbaş etiketi zaten kayıtlı: " + request.assetTag());
        }

        Asset asset = new Asset();
        fillAsset(asset, request.assetTag(), request.name(), request.type(), request.status(), request.serialNumber(),
                request.brand(), request.model(), request.departmentId(), request.assignedUserId());
        return toResponse(assetRepository.save(asset));
    }

    @Override
    public List<AssetResponse> findAll(AssetType type, AssetStatus status, String userEmail) {
        User currentUser = getUserByEmail(userEmail);
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
        if (currentUser.getRole() == UserRole.EMPLOYEE) {
            assets = assets.stream().filter(asset -> asset.getAssignedUser() != null
                    && asset.getAssignedUser().getId().equals(currentUser.getId())).toList();
        }
        return assets.stream().map(this::toResponse).toList();
    }

    @Override
    public AssetResponse findById(Long id, String userEmail) {
        return toResponse(getVisibleAsset(id, userEmail));
    }

    @Override
    public AssetResponse findByAssetTag(String assetTag, String userEmail) {
        Asset asset = assetRepository.findByAssetTagIgnoreCase(assetTag)
                .orElseThrow(() -> new ResourceNotFoundException("Bu etikete sahip demirbaş bulunamadı: " + assetTag));
        ensureAssetVisible(asset, getUserByEmail(userEmail));
        return toResponse(asset);
    }

    @Override
    @Transactional
    public AssetResponse update(Long id, UpdateAssetRequest request) {
        Asset asset = getAsset(id);
        if (!asset.getAssetTag().equalsIgnoreCase(request.assetTag())
                && assetRepository.existsByAssetTagIgnoreCase(request.assetTag())) {
            throw new DuplicateResourceException("Bu demirbaş etiketi zaten kayıtlı: " + request.assetTag());
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
            throw new BusinessRuleException("Destek talebi bulunan demirbaş silinemez.");
        }
        assetRepository.delete(asset);
    }

    private Asset getAsset(Long id) {
        return assetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bu numaraya sahip demirbaş bulunamadı. Numara: " + id));
    }

    private Asset getVisibleAsset(Long id, String userEmail) {
        Asset asset = getAsset(id);
        ensureAssetVisible(asset, getUserByEmail(userEmail));
        return asset;
    }

    private void ensureAssetVisible(Asset asset, User currentUser) {
        if (currentUser.getRole() == UserRole.EMPLOYEE
                && (asset.getAssignedUser() == null || !asset.getAssignedUser().getId().equals(currentUser.getId()))) {
            throw new ResourceNotFoundException("Bu demirbaş bulunamadı.");
        }
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("Oturum kullanıcısı bulunamadı."));
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
                .orElseThrow(() -> new ResourceNotFoundException("Bu numaraya sahip departman bulunamadı. Numara: " + departmentId));
    }

    private User findUserOrNull(Long userId) {
        if (userId == null) {
            return null;
        }
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Bu numaraya sahip kullanıcı bulunamadı. Numara: " + userId));
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
