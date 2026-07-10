package com.example.assetserviceticket.service;

import com.example.assetserviceticket.dto.request.CreateUserRequest;
import com.example.assetserviceticket.dto.request.UpdateUserRequest;
import com.example.assetserviceticket.dto.response.UserResponse;

import java.util.List;

public interface UserService {
    UserResponse create(CreateUserRequest request);

    List<UserResponse> findAll();

    UserResponse findById(Long id);

    UserResponse update(Long id, UpdateUserRequest request);

    UserResponse deactivate(Long id);
}
