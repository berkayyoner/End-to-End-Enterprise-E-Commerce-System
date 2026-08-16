package com.berkay.auth_service.sellerapplication.repository;

import com.berkay.auth_service.sellerapplication.entity.SellerApplication;
import com.berkay.auth_service.sellerapplication.entity.SellerApplicationStatus;
import com.berkay.auth_service.user.entity.AppUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerApplicationRepository extends JpaRepository<SellerApplication, Long> {

	boolean existsByAppUserAndStatus(AppUser appUser, SellerApplicationStatus status);

	Page<SellerApplication> findAllByStatus(SellerApplicationStatus status, Pageable pageable);
}
