package com.berkay.auth_service.exception;

public class SellerApplicationNotFoundException extends RuntimeException {

	public SellerApplicationNotFoundException(Long id) {
		super("No seller application with id: " + id);
	}
}
