package com.berkay.product_service.category.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * Request to create or update a MainCategory with translations.
 */
public record MainCategoryRequest(
		@NotEmpty(message = "translations list cannot be empty")
		@Valid
		List<TranslationInput> translations
) {
}
