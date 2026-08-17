package com.berkay.order_service.order.dto;

/**
 * Response indicating whether a buyer has purchased a specific product with a PAID order.
 * Used by product-service to gate review submissions.
 */
public record PurchaseVerificationResponse(
		boolean hasPurchased
) {
}
