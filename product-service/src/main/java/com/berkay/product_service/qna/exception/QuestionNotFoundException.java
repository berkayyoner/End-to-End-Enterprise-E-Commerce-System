package com.berkay.product_service.qna.exception;

/**
 * Thrown when a Q&A question is not found.
 */
public class QuestionNotFoundException extends QnaException {
	public QuestionNotFoundException(String message) {
		super(message);
	}
}
