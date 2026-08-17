package com.berkay.auth_service.exception;

public class SellerApplicationAlreadyReviewedException extends RuntimeException {

	public SellerApplicationAlreadyReviewedException(Long id) {
		super("Seller application " + id + " has already been reviewed");
	}
}
