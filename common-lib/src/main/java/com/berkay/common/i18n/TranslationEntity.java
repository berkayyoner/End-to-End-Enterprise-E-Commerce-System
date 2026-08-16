package com.berkay.common.i18n;

import com.berkay.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

/**
 * Base type for per-locale translation rows (e.g. product_translation, category_translation).
 * A concrete subclass adds the translatable columns (name, description, ...) and a
 * many-to-one back to the entity it translates, with a unique constraint on
 * (owning entity id, locale_code) so each entity has at most one row per language.
 */
@MappedSuperclass
public abstract class TranslationEntity extends BaseEntity {

	@Column(name = "locale_code", nullable = false, length = 10)
	private String localeCode;

	public String getLocaleCode() {
		return localeCode;
	}

	public void setLocaleCode(String localeCode) {
		this.localeCode = localeCode;
	}
}
