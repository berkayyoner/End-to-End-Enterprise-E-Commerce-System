package com.berkay.auth_service.exception;

public class AppUserNotFoundException extends RuntimeException {

	public AppUserNotFoundException(Long id) {
		super("No account with id: " + id);
	}
}
