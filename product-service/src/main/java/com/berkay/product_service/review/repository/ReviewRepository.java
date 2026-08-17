package com.berkay.product_service.review.repository;

import com.berkay.product_service.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

	/**
	 * Find all non-deleted reviews for a product.
	 */
	List<Review> findByProductIdAndDeletedFalse(Long productId);

	/**
	 * Check if a specific reviewer has already reviewed a product.
	 */
	Optional<Review> findByProductIdAndReviewerIdAndDeletedFalse(Long productId, String reviewerId);

	/**
	 * Calculate average rating for a product (excluding deleted reviews).
	 */
	double findAverageRatingByProductIdAndDeletedFalse(Long productId);

	/**
	 * Count non-deleted reviews for a product.
	 */
	long countByProductIdAndDeletedFalse(Long productId);
}
