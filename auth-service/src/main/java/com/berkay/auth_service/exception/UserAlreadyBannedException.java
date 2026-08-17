package com.berkay.auth_service.exception;

public class UserAlreadyBannedException extends RuntimeException {

	public UserAlreadyBannedException() {
		super("This account has already been banned");
	}
}
