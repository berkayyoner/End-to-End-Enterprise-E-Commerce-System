package com.berkay.auth_service.seller.repository;

import com.berkay.auth_service.seller.entity.SellerEarnings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SellerEarningsRepository extends JpaRepository<SellerEarnings, Long> {
	Optional<SellerEarnings> findBySellerId(Long sellerId);
}
