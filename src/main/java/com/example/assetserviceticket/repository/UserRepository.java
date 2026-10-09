package com.example.assetserviceticket.repository;

import com.example.assetserviceticket.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmailIgnoreCase(String email);

    boolean existsByDepartmentId(Long departmentId);

    Optional<User> findByEmailIgnoreCase(String email);
}
