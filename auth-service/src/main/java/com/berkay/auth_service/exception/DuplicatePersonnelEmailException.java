package com.berkay.auth_service.exception;

public class DuplicatePersonnelEmailException extends RuntimeException {

	public DuplicatePersonnelEmailException(String email) {
		super("A personnel account with email '" + email + "' already exists");
	}
}
