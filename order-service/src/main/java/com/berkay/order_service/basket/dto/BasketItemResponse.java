package com.berkay.order_service.basket.dto;

public record BasketItemResponse(
	Long id,
	Long productId,
	Integer quantity
) {}
