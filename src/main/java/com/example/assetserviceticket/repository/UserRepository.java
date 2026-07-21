package com.example.assetserviceticket.repository;

import com.example.assetserviceticket.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmailIgnoreCase(String email);

    boolean existsByDepartmentId(Long departmentId);
}
