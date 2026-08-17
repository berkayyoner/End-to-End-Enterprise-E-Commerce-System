package com.berkay.product_service.favorite.exception;

public abstract class FavoriteException extends RuntimeException {

	public FavoriteException(String message) {
		super(message);
	}

	public FavoriteException(String message, Throwable cause) {
		super(message, cause);
	}
}
