package com.berkay.order_service.exception;

public class InvalidCouponException extends RuntimeException {
	public InvalidCouponException(String message) {
		super(message);
	}
}
