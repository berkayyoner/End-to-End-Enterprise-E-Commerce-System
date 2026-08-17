package com.berkay.common.i18n;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class TranslationResolverTest {

	@Test
	void resolvesExactLocaleMatch() {
		List<TestTranslation> translations = List.of(new TestTranslation("tr"), new TestTranslation("en"));

		Optional<TestTranslation> result = TranslationResolver.resolve(translations, "en");

		assertThat(result).isPresent();
		assertThat(result.get().getLocaleCode()).isEqualTo("en");
	}

	@Test
	void fallsBackToDefaultLocaleWhenRequestedMissing() {
		List<TestTranslation> translations = List.of(new TestTranslation("tr"), new TestTranslation("de"));

		Optional<TestTranslation> result = TranslationResolver.resolve(translations, "fr");

		assertThat(result).isPresent();
		assertThat(result.get().getLocaleCode()).isEqualTo("tr");
	}

	@Test
	void fallsBackToAnyAvailableWhenNeitherRequestedNorDefaultExists() {
		List<TestTranslation> translations = List.of(new TestTranslation("de"));

		Optional<TestTranslation> result = TranslationResolver.resolve(translations, "fr");

		assertThat(result).isPresent();
		assertThat(result.get().getLocaleCode()).isEqualTo("de");
	}

	@Test
	void returnsEmptyWhenNoTranslationsExist() {
		Optional<TestTranslation> result = TranslationResolver.resolve(List.of(), "tr");

		assertThat(result).isEmpty();
	}

	private static final class TestTranslation extends TranslationEntity {
		TestTranslation(String localeCode) {
			setLocaleCode(localeCode);
		}
	}
}
