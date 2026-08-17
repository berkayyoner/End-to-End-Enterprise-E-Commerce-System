package com.berkay.product_service.product.exception;

/**
 * Thrown when a user tries to modify a product they don't own.
 */
public class ProductOwnershipException extends RuntimeException {
	public ProductOwnershipException(String message) {
		super(message);
	}
}
