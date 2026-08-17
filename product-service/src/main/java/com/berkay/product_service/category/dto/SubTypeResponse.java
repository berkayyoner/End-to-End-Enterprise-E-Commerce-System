package com.berkay.product_service.category.dto;

import com.berkay.common.i18n.TranslationResolver;
import com.berkay.product_service.category.entity.SubType;
import com.berkay.product_service.category.entity.SubTypeTranslation;

/**
 * Response containing a SubType with its resolved translation for the requested locale.
 */
public record SubTypeResponse(
		Long id,
		Long mainCategoryId,
		String name,
		String description
) {
	public static SubTypeResponse from(SubType subType, String requestedLocale) {
		SubTypeTranslation resolved = TranslationResolver.resolve(subType.getTranslations(), requestedLocale)
				.orElse(null);

		String name = resolved != null ? resolved.getName() : "Untranslated";
		String description = resolved != null ? resolved.getDescription() : null;

		return new SubTypeResponse(subType.getId(), subType.getMainCategory().getId(), name, description);
	}
}
