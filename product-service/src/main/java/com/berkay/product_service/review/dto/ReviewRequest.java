package com.berkay.product_service.review.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * Request to submit a product review.
 */
public record ReviewRequest(
		@Min(1) @Max(5)
		int rating,

		String comment
) {
}
