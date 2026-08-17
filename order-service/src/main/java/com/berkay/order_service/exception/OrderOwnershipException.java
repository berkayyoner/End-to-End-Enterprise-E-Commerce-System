package com.berkay.order_service.exception;

public class OrderOwnershipException extends RuntimeException {
	public OrderOwnershipException(String message) {
		super(message);
	}
}
