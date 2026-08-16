package com.berkay.auth_service.personnel.repository;

import com.berkay.auth_service.personnel.entity.PermissionGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PermissionGroupRepository extends JpaRepository<PermissionGroup, Long> {

	Optional<PermissionGroup> findByName(String name);

	boolean existsByName(String name);

	boolean existsByNameAndDeletedFalse(String name);

	@Query("select distinct g from PermissionGroup g left join fetch g.entries where g.deleted = false")
	List<PermissionGroup> findAllActiveWithEntries();

	@Query("select g from PermissionGroup g left join fetch g.entries where g.id = :id and g.deleted = false")
	Optional<PermissionGroup> findActiveByIdWithEntries(@Param("id") Long id);
}
