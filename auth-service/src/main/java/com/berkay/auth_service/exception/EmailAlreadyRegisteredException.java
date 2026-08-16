package com.berkay.auth_service.exception;

public class EmailAlreadyRegisteredException extends RuntimeException {

	public EmailAlreadyRegisteredException(String email) {
		super("An account already exists for email: " + email);
	}
}
