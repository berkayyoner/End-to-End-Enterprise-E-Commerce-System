package com.berkay.order_service.exception;

public class BasketNotFoundException extends RuntimeException {
	public BasketNotFoundException(String message) {
		super(message);
	}
}
