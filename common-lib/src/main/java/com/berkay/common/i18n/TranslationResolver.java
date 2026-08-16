package com.berkay.common.i18n;

import java.util.Collection;
import java.util.Optional;

/**
 * Resolves the best-matching translation for a requested locale: exact match, then the
 * platform default ({@link LocaleCodes#DEFAULT_LOCALE}), then whatever translation exists.
 * Every translatable entity (product, category, campaign, ...) resolves through this same
 * chain, so adding a new language is purely a data change - no service code changes.
 */
public final class TranslationResolver {

	private TranslationResolver() {
	}

	public static <T extends TranslationEntity> Optional<T> resolve(Collection<T> translations, String requestedLocale) {
		if (translations == null || translations.isEmpty()) {
			return Optional.empty();
		}

		Optional<T> exact = findByLocale(translations, requestedLocale);
		if (exact.isPresent()) {
			return exact;
		}

		Optional<T> defaultLocale = findByLocale(translations, LocaleCodes.DEFAULT_LOCALE);
		if (defaultLocale.isPresent()) {
			return defaultLocale;
		}

		return translations.stream().findFirst();
	}

	private static <T extends TranslationEntity> Optional<T> findByLocale(Collection<T> translations, String localeCode) {
		if (localeCode == null) {
			return Optional.empty();
		}
		return translations.stream()
				.filter(t -> localeCode.equalsIgnoreCase(t.getLocaleCode()))
				.findFirst();
	}
}
