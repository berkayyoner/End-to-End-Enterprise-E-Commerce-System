package com.berkay.order_service.order.dto;

import java.math.BigDecimal;

public record OrderItemResponse(
	Long productId,
	String sellerId,
	Integer quantity,
	BigDecimal unitPrice
) {}
