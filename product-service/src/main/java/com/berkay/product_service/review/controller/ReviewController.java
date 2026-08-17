package com.berkay.product_service.review.controller;

import com.berkay.product_service.review.dto.ReviewRequest;
import com.berkay.product_service.review.dto.ReviewResponse;
import com.berkay.product_service.review.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST endpoints for product reviews.
 * POST requires authentication (verified buyer), GET is public.
 */
@RestController
@RequestMapping("/products")
public class ReviewController {

	private final ReviewService reviewService;

	public ReviewController(ReviewService reviewService) {
		this.reviewService = reviewService;
	}

	/**
	 * Submit a review for a product.
	 * Requires authentication (buyer who has purchased the product).
	 */
	@PostMapping("/{productId}/reviews")
	public ResponseEntity<ReviewResponse> submitReview(
			@PathVariable Long productId,
			Authentication authentication,
			@Valid @RequestBody ReviewRequest request) {
		String reviewerId = authentication.getName();
		ReviewResponse review = reviewService.submitReview(productId, reviewerId, request);
		return ResponseEntity.status(HttpStatus.CREATED).body(review);
	}

	/**
	 * Get all reviews for a product.
	 * Public endpoint (no authentication required).
	 */
	@GetMapping("/{productId}/reviews")
	public ResponseEntity<List<ReviewResponse>> getProductReviews(
			@PathVariable Long productId) {
		List<ReviewResponse> reviews = reviewService.getProductReviews(productId);
		return ResponseEntity.ok(reviews);
	}
}
