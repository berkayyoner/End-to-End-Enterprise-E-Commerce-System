package com.berkay.usermanagement.repository;

import com.berkay.usermanagement.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
}
