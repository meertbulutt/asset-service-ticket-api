package com.example.assetserviceticket.service.impl;

import com.example.assetserviceticket.dto.request.CreateDepartmentRequest;
import com.example.assetserviceticket.dto.request.UpdateDepartmentRequest;
import com.example.assetserviceticket.dto.response.DepartmentResponse;
import com.example.assetserviceticket.entity.Department;
import com.example.assetserviceticket.exception.BusinessRuleException;
import com.example.assetserviceticket.exception.DuplicateResourceException;
import com.example.assetserviceticket.exception.ResourceNotFoundException;
import com.example.assetserviceticket.repository.AssetRepository;
import com.example.assetserviceticket.repository.DepartmentRepository;
import com.example.assetserviceticket.repository.UserRepository;
import com.example.assetserviceticket.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;
    private final AssetRepository assetRepository;

    @Override
    @Transactional
    public DepartmentResponse create(CreateDepartmentRequest request) {
        if (departmentRepository.existsByNameIgnoreCase(request.name())) {
            throw new DuplicateResourceException("Department already exists with name: " + request.name());
        }

        Department department = new Department();
        department.setName(request.name());
        department.setDescription(request.description());
        return toResponse(departmentRepository.save(department));
    }

    @Override
    public List<DepartmentResponse> findAll() {
        return departmentRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public DepartmentResponse findById(Long id) {
        return toResponse(getDepartment(id));
    }

    @Override
    @Transactional
    public DepartmentResponse update(Long id, UpdateDepartmentRequest request) {
        Department department = getDepartment(id);
        if (!department.getName().equalsIgnoreCase(request.name())
                && departmentRepository.existsByNameIgnoreCase(request.name())) {
            throw new DuplicateResourceException("Department already exists with name: " + request.name());
        }

        department.setName(request.name());
        department.setDescription(request.description());
        return toResponse(departmentRepository.save(department));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Department department = getDepartment(id);
        if (userRepository.existsByDepartmentId(id) || assetRepository.existsByDepartmentId(id)) {
            throw new BusinessRuleException("Department cannot be deleted while users or assets are assigned to it");
        }
        departmentRepository.delete(department);
    }

    private Department getDepartment(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + id));
    }

    private DepartmentResponse toResponse(Department department) {
        return new DepartmentResponse(
                department.getId(),
                department.getName(),
                department.getDescription(),
                department.getCreatedAt()
        );
    }
}
