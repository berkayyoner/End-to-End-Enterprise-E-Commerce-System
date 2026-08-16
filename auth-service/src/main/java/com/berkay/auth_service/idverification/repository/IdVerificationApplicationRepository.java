package com.berkay.auth_service.idverification.repository;

import com.berkay.auth_service.idverification.entity.IdVerificationApplication;
import com.berkay.auth_service.idverification.entity.IdVerificationStatus;
import com.berkay.auth_service.user.entity.AppUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IdVerificationApplicationRepository extends JpaRepository<IdVerificationApplication, Long> {

	boolean existsByAppUserAndStatus(AppUser appUser, IdVerificationStatus status);

	Page<IdVerificationApplication> findAllByStatus(IdVerificationStatus status, Pageable pageable);

	List<IdVerificationApplication> findAllByAppUser(AppUser appUser);
}
