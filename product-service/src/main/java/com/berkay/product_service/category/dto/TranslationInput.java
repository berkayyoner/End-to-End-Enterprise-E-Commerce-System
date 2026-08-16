package com.berkay.product_service.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Common request input for a single locale's translation (name + optional description).
 */
public record TranslationInput(
		@NotBlank(message = "localeCode cannot be blank")
		@Size(min = 2, max = 10, message = "localeCode must be 2-10 characters")
		String localeCode,

		@NotBlank(message = "name cannot be blank")
		@Size(max = 255, message = "name must be at most 255 characters")
		String name,

		@Size(max = 1000, message = "description must be at most 1000 characters")
		String description
) {
}
