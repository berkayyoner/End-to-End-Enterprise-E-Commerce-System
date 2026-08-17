package com.berkay.auth_service.exception;

public class IdVerificationApplicationNotFoundException extends RuntimeException {

	public IdVerificationApplicationNotFoundException(Long id) {
		super("No ID verification application with id: " + id);
	}
}
