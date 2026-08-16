package com.berkay.auth_service.personnel.repository;

import com.berkay.auth_service.personnel.entity.PermissionGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PermissionGroupRepository extends JpaRepository<PermissionGroup, Long> {

	Optional<PermissionGroup> findByName(String name);

	boolean existsByName(String name);
}
