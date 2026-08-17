package com.berkay.product_service.follow.repository;

import com.berkay.product_service.follow.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {

	@Query("SELECT f FROM Follow f WHERE f.buyerId = ?1 AND f.sellerId = ?2 AND f.deleted = false")
	Optional<Follow> findByBuyerIdAndSellerIdAndDeletedFalse(String buyerId, String sellerId);

	@Query("SELECT COUNT(f) FROM Follow f WHERE f.sellerId = ?1 AND f.deleted = false")
	long countBySellerIdAndDeletedFalse(String sellerId);
}
