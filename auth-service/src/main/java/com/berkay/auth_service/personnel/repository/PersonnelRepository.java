package com.berkay.auth_service.personnel.repository;

import com.berkay.auth_service.personnel.entity.Personnel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PersonnelRepository extends JpaRepository<Personnel, Long> {

	Optional<Personnel> findByEmail(String email);

	boolean existsByEmail(String email);

	/**
	 * Fetches the permission group and its entries in the same query so callers (the personnel
	 * login's UserDetailsService and the JWT token customizer) can read {@code toCodes()} outside
	 * of any transaction without a LazyInitializationException.
	 */
	@Query("select p from Personnel p join fetch p.permissionGroup pg left join fetch pg.entries where p.email = :email")
	Optional<Personnel> findByEmailWithPermissions(@Param("email") String email);

	/**
	 * Fetches the non-deleted personnel with their permission group and entries, for the
	 * /personnel/me endpoint (task 1.9) to return the current personnel's data.
	 */
	@Query("select p from Personnel p join fetch p.permissionGroup pg left join fetch pg.entries where p.email = :email and p.deleted = false")
	Optional<Personnel> findByEmailWithPermissionsAndNotDeleted(@Param("email") String email);
}
