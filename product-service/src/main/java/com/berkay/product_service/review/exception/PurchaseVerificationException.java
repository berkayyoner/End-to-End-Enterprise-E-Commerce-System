package com.berkay.product_service.review.exception;

/**
 * Thrown when purchase verification fails (order-service unreachable or buyer hasn't purchased).
 * This is a permission denial (fail-closed), not a recoverable error.
 */
public class PurchaseVerificationException extends ReviewException {
	public PurchaseVerificationException(String message) {
		super(message);
	}

	public PurchaseVerificationException(String message, Throwable cause) {
		super(message, cause);
	}
}
