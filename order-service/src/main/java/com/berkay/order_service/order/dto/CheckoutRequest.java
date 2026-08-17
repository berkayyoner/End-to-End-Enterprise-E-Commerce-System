package com.berkay.order_service.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record CheckoutRequest(
	@NotNull(message = "Payment information is required")
	@Valid
	PaymentData payment,

	String couponCode
) {

	public record PaymentData(
		String cardNumber,
		Integer expiryMonth,
		Integer expiryYear,
		String cvv,
		String cardHolderName,
		Boolean saveCard
	) {}
}
