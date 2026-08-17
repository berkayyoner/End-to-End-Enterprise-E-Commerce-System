package com.berkay.product_service.category.exception;

public class CategoryNotFoundException extends RuntimeException {
	public CategoryNotFoundException(String message) {
		super(message);
	}
}
