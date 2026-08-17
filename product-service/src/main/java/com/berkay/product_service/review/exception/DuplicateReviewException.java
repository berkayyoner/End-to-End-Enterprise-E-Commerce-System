package com.berkay.product_service.review.exception;

/**
 * Thrown when a reviewer attempts to submit a duplicate review for the same product.
 */
public class DuplicateReviewException extends ReviewException {
	public DuplicateReviewException(String message) {
		super(message);
	}
}
