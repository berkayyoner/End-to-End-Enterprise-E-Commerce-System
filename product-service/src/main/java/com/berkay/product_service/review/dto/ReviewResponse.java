package com.berkay.product_service.review.dto;

import com.berkay.product_service.review.entity.Review;
import java.time.Instant;

/**
 * Response containing a product review.
 */
public record ReviewResponse(
		Long id,
		String reviewerId,
		int rating,
		String comment,
		Instant createdAt
) {
	public static ReviewResponse from(Review review) {
		return new ReviewResponse(
				review.getId(),
				review.getReviewerId(),
				review.getRating(),
				review.getComment(),
				review.getCreatedAt()
		);
	}
}
