package com.berkay.order_service.payment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record PaymentRequest(
	@NotBlank(message = "Card number is required")
	@Pattern(regexp = "^\\d{13,19}$", message = "Card number must be 13-19 digits")
	String cardNumber,

	@NotNull(message = "Expiry month is required")
	Integer expiryMonth,

	@NotNull(message = "Expiry year is required")
	Integer expiryYear,

	@NotBlank(message = "CVV is required")
	@Pattern(regexp = "^\\d{3,4}$", message = "CVV must be 3-4 digits")
	String cvv,

	@NotBlank(message = "Card holder name is required")
	String cardHolderName,

	Boolean saveCard
) {}
