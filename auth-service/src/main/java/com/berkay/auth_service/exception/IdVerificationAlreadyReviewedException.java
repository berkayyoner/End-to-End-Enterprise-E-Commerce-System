package com.berkay.auth_service.exception;

public class IdVerificationAlreadyReviewedException extends RuntimeException {

	public IdVerificationAlreadyReviewedException(Long id) {
		super("ID verification application " + id + " has already been reviewed");
	}
}
