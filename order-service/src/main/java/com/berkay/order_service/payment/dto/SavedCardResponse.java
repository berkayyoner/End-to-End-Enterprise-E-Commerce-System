package com.berkay.order_service.payment.dto;

public record SavedCardResponse(
	String cardNumberLast4,
	Integer expiryMonth,
	Integer expiryYear,
	String cardHolderName
) {}
