package com.berkay.order_service.exception;

public class OrderNotFoundException extends RuntimeException {
	public OrderNotFoundException(String message) {
		super(message);
	}
}
