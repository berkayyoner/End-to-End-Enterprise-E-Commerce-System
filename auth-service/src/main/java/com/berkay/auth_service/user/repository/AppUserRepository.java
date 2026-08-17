package com.berkay.auth_service.user.repository;

import com.berkay.auth_service.user.entity.AppUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {

	Optional<AppUser> findByEmail(String email);

	boolean existsByEmail(String email);

	@Query("SELECT u FROM AppUser u WHERE u.deleted = false ORDER BY u.createdAt DESC")
	Page<AppUser> findAllActive(Pageable pageable);
}
