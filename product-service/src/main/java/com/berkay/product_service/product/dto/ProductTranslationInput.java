package com.berkay.product_service.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request input for a single locale's product translation.
 */
public record ProductTranslationInput(
		@NotBlank(message = "localeCode cannot be blank")
		@Size(min = 2, max = 10, message = "localeCode must be 2-10 characters")
		String localeCode,

		@NotBlank(message = "name cannot be blank")
		@Size(max = 255, message = "name must be at most 255 characters")
		String name,

		@Size(max = 500, message = "shortDescription must be at most 500 characters")
		String shortDescription,

		@Size(max = 2000, message = "longDescription must be at most 2000 characters")
		String longDescription
) {
}
