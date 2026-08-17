package com.berkay.order_service.basket.dto;

import java.util.List;

public record BasketResponse(
	Long id,
	List<BasketItemResponse> items
) {}
