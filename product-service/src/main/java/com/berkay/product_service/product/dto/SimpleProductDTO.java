package com.berkay.product_service.product.dto;

import java.math.BigDecimal;

/**
 * Simplified product representation for similar/recommended/bought-together lists.
 */
public record SimpleProductDTO(
		Long id,
		String name,
		BigDecimal price,
		String photoUrl,
		double averageRating
) {
}
