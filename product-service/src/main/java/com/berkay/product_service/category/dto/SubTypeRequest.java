package com.berkay.product_service.category.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * Request to create or update a SubType with translations.
 */
public record SubTypeRequest(
		@NotNull(message = "mainCategoryId cannot be null")
		Long mainCategoryId,

		@NotEmpty(message = "translations list cannot be empty")
		@Valid
		List<TranslationInput> translations
) {
}
