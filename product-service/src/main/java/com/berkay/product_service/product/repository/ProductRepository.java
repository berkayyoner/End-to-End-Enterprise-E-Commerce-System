package com.berkay.product_service.product.repository;

import com.berkay.product_service.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

	@Query("SELECT p FROM Product p LEFT JOIN FETCH p.translations " +
			"LEFT JOIN FETCH p.photos LEFT JOIN FETCH p.keyFeatures " +
			"WHERE p.id = :id AND p.deleted = false")
	Optional<Product> findActiveById(@Param("id") Long id);

	@Query("SELECT p FROM Product p LEFT JOIN FETCH p.translations " +
			"LEFT JOIN FETCH p.photos LEFT JOIN FETCH p.keyFeatures " +
			"WHERE p.deleted = false ORDER BY p.id")
	List<Product> findAllActive();

	@Query("SELECT p FROM Product p LEFT JOIN FETCH p.translations " +
			"LEFT JOIN FETCH p.photos LEFT JOIN FETCH p.keyFeatures " +
			"WHERE p.sellerId = :sellerId AND p.deleted = false ORDER BY p.id")
	List<Product> findActiveBySellerId(@Param("sellerId") Long sellerId);

	boolean existsByIdAndDeletedFalse(Long id);
}
