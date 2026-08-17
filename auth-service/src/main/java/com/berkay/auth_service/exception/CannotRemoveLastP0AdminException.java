package com.berkay.auth_service.exception;

public class CannotRemoveLastP0AdminException extends RuntimeException {

	public CannotRemoveLastP0AdminException() {
		super("Cannot remove the last personnel account with P0 (Permissions) access - " +
				"at least one P0 admin must exist to manage the system");
	}
}
