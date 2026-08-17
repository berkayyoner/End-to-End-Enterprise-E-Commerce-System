package com.berkay.product_service.qna.exception;

/**
 * Base exception for Q&A-related errors.
 */
public class QnaException extends RuntimeException {
	public QnaException(String message) {
		super(message);
	}

	public QnaException(String message, Throwable cause) {
		super(message, cause);
	}
}
