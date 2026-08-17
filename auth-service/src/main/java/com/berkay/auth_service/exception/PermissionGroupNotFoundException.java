package com.berkay.auth_service.exception;

public class PermissionGroupNotFoundException extends RuntimeException {

	public PermissionGroupNotFoundException(Long id) {
		super("No permission group with id: " + id);
	}
}
