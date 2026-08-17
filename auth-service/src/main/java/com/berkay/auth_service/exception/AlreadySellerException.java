package com.berkay.auth_service.exception;

public class AlreadySellerException extends RuntimeException {

	public AlreadySellerException() {
		super("This account is already an approved seller");
	}
}
