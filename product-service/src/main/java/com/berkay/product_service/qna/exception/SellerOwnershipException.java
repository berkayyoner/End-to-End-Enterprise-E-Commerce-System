package com.berkay.product_service.qna.exception;

/**
 * Thrown when a non-owning seller attempts to answer a question on a product they don't own.
 */
public class SellerOwnershipException extends QnaException {
	public SellerOwnershipException(String message) {
		super(message);
	}
}
