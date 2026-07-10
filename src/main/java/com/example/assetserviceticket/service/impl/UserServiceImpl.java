package com.example.assetserviceticket.service.impl;

import com.example.assetserviceticket.dto.request.CreateUserRequest;
import com.example.assetserviceticket.dto.request.UpdateUserRequest;
import com.example.assetserviceticket.dto.response.UserResponse;
import com.example.assetserviceticket.entity.Department;
import com.example.assetserviceticket.entity.User;
import com.example.assetserviceticket.exception.DuplicateResourceException;
import com.example.assetserviceticket.exception.ResourceNotFoundException;
import com.example.assetserviceticket.repository.DepartmentRepository;
import com.example.assetserviceticket.repository.UserRepository;
import com.example.assetserviceticket.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;

    @Override
    public UserResponse create(CreateUserRequest request) {
        if (userRepository.existsByEmailIgnoreCase(request.email())) {
            throw new DuplicateResourceException("User already exists with email: " + request.email());
        }

        User user = new User();
        user.setFullName(request.fullName());
        user.setEmail(request.email());
        user.setRole(request.role());
        user.setDepartment(findDepartmentOrNull(request.departmentId()));
        return toResponse(userRepository.save(user));
    }

    @Override
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public UserResponse findById(Long id) {
        return toResponse(getUser(id));
    }

    @Override
    public UserResponse update(Long id, UpdateUserRequest request) {
        User user = getUser(id);
        if (!user.getEmail().equalsIgnoreCase(request.email()) && userRepository.existsByEmailIgnoreCase(request.email())) {
            throw new DuplicateResourceException("User already exists with email: " + request.email());
        }

        user.setFullName(request.fullName());
        user.setEmail(request.email());
        user.setRole(request.role());
        user.setDepartment(findDepartmentOrNull(request.departmentId()));
        if (request.active() != null) {
            user.setActive(request.active());
        }
        return toResponse(userRepository.save(user));
    }

    @Override
    public UserResponse deactivate(Long id) {
        User user = getUser(id);
        user.setActive(false);
        return toResponse(userRepository.save(user));
    }

    private User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    private Department findDepartmentOrNull(Long departmentId) {
        if (departmentId == null) {
            return null;
        }
        return departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + departmentId));
    }

    private UserResponse toResponse(User user) {
        Department department = user.getDepartment();
        return new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole(),
                department == null ? null : department.getId(),
                department == null ? null : department.getName(),
                user.isActive(),
                user.getCreatedAt()
        );
    }
}
