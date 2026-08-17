package com.berkay.product_service.product.exception;

/**
 * Thrown when a Product is not found.
 */
public class ProductNotFoundException extends RuntimeException {
	public ProductNotFoundException(String message) {
		super(message);
	}
}
