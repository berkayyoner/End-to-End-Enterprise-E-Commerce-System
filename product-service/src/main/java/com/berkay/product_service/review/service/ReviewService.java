package com.berkay.product_service.review.service;

import com.berkay.product_service.product.entity.Product;
import com.berkay.product_service.product.repository.ProductRepository;
import com.berkay.product_service.review.client.OrderServiceClient;
import com.berkay.product_service.review.dto.ReviewRequest;
import com.berkay.product_service.review.dto.ReviewResponse;
import com.berkay.product_service.review.entity.Review;
import com.berkay.product_service.review.exception.DuplicateReviewException;
import com.berkay.product_service.review.exception.PurchaseVerificationException;
import com.berkay.product_service.review.repository.ReviewRepository;
import com.berkay.product_service.product.exception.ProductNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for managing product reviews.
 * Enforces review eligibility: buyer must have a PAID order containing the product.
 */
@Service
@Transactional
public class ReviewService {

	private final ReviewRepository reviewRepository;
	private final ProductRepository productRepository;
	private final OrderServiceClient orderServiceClient;

	public ReviewService(
			ReviewRepository reviewRepository,
			ProductRepository productRepository,
			OrderServiceClient orderServiceClient) {
		this.reviewRepository = reviewRepository;
		this.productRepository = productRepository;
		this.orderServiceClient = orderServiceClient;
	}

	/**
	 * Submit a review for a product.
	 * Verifies: (1) product exists, (2) no duplicate review by this reviewer,
	 * (3) buyer has a PAID order containing this product.
	 *
	 * @param productId the product being reviewed
	 * @param reviewerId the buyer's user ID (from JWT sub)
	 * @param request the review (rating 1-5, optional comment)
	 * @return the persisted review
	 * @throws ProductNotFoundException if product doesn't exist
	 * @throws DuplicateReviewException if reviewer already reviewed this product
	 * @throws PurchaseVerificationException if buyer hasn't purchased the product (fail-closed)
	 */
	public ReviewResponse submitReview(Long productId, String reviewerId, ReviewRequest request) {
		// Verify product exists
		Product product = productRepository.findActiveById(productId)
				.orElseThrow(() -> new ProductNotFoundException("Product not found: " + productId));

		// Check for duplicate review
		reviewRepository.findByProductIdAndReviewerIdAndDeletedFalse(productId, reviewerId)
				.ifPresent(existing -> {
					throw new DuplicateReviewException(
							"You have already reviewed this product"
					);
				});

		// Verify purchase (fail-closed)
		if (!orderServiceClient.hasPurchased(reviewerId, productId)) {
			throw new PurchaseVerificationException(
					"You must have purchased this product to review it"
			);
		}

		// Create and persist review
		Review review = new Review(product, reviewerId, request.rating(), request.comment());
		Review saved = reviewRepository.save(review);

		return ReviewResponse.from(saved);
	}

	/**
	 * Get all reviews for a product (excludes deleted reviews).
	 */
	@Transactional(readOnly = true)
	public List<ReviewResponse> getProductReviews(Long productId) {
		// Verify product exists
		productRepository.findActiveById(productId)
				.orElseThrow(() -> new ProductNotFoundException("Product not found: " + productId));

		return reviewRepository.findByProductIdAndDeletedFalse(productId).stream()
				.map(ReviewResponse::from)
				.toList();
	}

	/**
	 * Calculate the average rating for a product.
	 */
	@Transactional(readOnly = true)
	public double getAverageRating(Long productId) {
		return reviewRepository.findAverageRatingByProductIdAndDeletedFalse(productId);
	}

	/**
	 * Get the count of reviews for a product.
	 */
	@Transactional(readOnly = true)
	public long getRatingCount(Long productId) {
		return reviewRepository.countByProductIdAndDeletedFalse(productId);
	}
}
