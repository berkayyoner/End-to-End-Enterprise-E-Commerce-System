package com.berkay.auth_service.exception;

public class DuplicatePermissionGroupNameException extends RuntimeException {

	public DuplicatePermissionGroupNameException(String name) {
		super("A permission group named '" + name + "' already exists");
	}
}
