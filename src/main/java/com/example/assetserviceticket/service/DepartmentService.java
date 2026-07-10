package com.example.assetserviceticket.service;

import com.example.assetserviceticket.dto.request.CreateDepartmentRequest;
import com.example.assetserviceticket.dto.request.UpdateDepartmentRequest;
import com.example.assetserviceticket.dto.response.DepartmentResponse;

import java.util.List;

public interface DepartmentService {
    DepartmentResponse create(CreateDepartmentRequest request);

    List<DepartmentResponse> findAll();

    DepartmentResponse findById(Long id);

    DepartmentResponse update(Long id, UpdateDepartmentRequest request);

    void delete(Long id);
}
