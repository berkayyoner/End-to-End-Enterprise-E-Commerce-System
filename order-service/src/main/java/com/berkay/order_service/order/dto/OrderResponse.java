package com.berkay.order_service.order.dto;

import com.berkay.order_service.order.entity.OrderStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponse(
	Long id,
	OrderStatus status,
	BigDecimal totalAmount,
	List<OrderItemResponse> items,
	Instant createdAt
) {}
