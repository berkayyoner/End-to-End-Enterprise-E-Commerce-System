package com.berkay.product_service.favorite.repository;

import com.berkay.product_service.favorite.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

	@Query("SELECT f FROM Favorite f WHERE f.buyerId = ?1 AND f.product.id = ?2 AND f.deleted = false")
	Optional<Favorite> findByBuyerIdAndProductIdAndDeletedFalse(String buyerId, Long productId);

	@Query("SELECT f FROM Favorite f WHERE f.buyerId = ?1 AND f.deleted = false ORDER BY f.createdAt DESC")
	List<Favorite> findByBuyerIdAndDeletedFalse(String buyerId);

	@Query("SELECT COUNT(f) FROM Favorite f WHERE f.product.id = ?1 AND f.deleted = false")
	long countByProductIdAndDeletedFalse(Long productId);
}
