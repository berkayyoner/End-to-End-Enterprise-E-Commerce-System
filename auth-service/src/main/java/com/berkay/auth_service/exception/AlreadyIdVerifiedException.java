package com.berkay.auth_service.exception;

public class AlreadyIdVerifiedException extends RuntimeException {

	public AlreadyIdVerifiedException() {
		super("This account is already ID-verified");
	}
}
