package com.berkay.auth_service.exception;

public class DuplicatePendingSellerApplicationException extends RuntimeException {

	public DuplicatePendingSellerApplicationException() {
		super("A seller application is already pending review");
	}
}
