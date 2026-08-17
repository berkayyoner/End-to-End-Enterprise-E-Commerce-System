package com.berkay.product_service.review.exception;

/**
 * Base exception for review-related errors.
 */
public class ReviewException extends RuntimeException {
	public ReviewException(String message) {
		super(message);
	}

	public ReviewException(String message, Throwable cause) {
		super(message, cause);
	}
}
