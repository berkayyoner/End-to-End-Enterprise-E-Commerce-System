package com.berkay.auth_service.exception;

public class IdNotVerifiedException extends RuntimeException {

	public IdNotVerifiedException() {
		super("Your ID must be verified before you can apply to become a seller");
	}
}
