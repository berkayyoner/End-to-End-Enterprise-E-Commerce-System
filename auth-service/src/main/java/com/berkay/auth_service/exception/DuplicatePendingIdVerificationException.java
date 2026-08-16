package com.berkay.auth_service.exception;

public class DuplicatePendingIdVerificationException extends RuntimeException {

	public DuplicatePendingIdVerificationException() {
		super("An ID verification application is already pending review");
	}
}
