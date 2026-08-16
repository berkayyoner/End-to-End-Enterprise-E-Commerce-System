package com.berkay.product_service.category.dto;

import com.berkay.common.i18n.TranslationResolver;
import com.berkay.product_service.category.entity.MainCategory;
import com.berkay.product_service.category.entity.MainCategoryTranslation;

/**
 * Response containing a MainCategory with its resolved translation for the requested locale.
 */
public record MainCategoryResponse(
		Long id,
		String name,
		String description
) {
	public static MainCategoryResponse from(MainCategory category, String requestedLocale) {
		MainCategoryTranslation resolved = TranslationResolver.resolve(category.getTranslations(), requestedLocale)
				.orElse(null);

		String name = resolved != null ? resolved.getName() : "Untranslated";
		String description = resolved != null ? resolved.getDescription() : null;

		return new MainCategoryResponse(category.getId(), name, description);
	}
}
