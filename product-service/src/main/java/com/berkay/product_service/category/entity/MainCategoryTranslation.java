package com.berkay.product_service.category.entity;

import com.berkay.common.i18n.TranslationEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * Translation of a MainCategory's name and description for a specific locale.
 * Extends common-lib's TranslationEntity which provides locale_code and audit/soft-delete columns.
 * Unique constraint ensures each category has at most one translation per locale.
 */
@Entity
@Table(name = "main_category_translation",
		uniqueConstraints = {
			@UniqueConstraint(name = "uk_main_category_translation_category_locale",
					columnNames = {"main_category_id", "locale_code"})
		})
public class MainCategoryTranslation extends TranslationEntity {

	@ManyToOne(optional = false)
	@JoinColumn(name = "main_category_id", nullable = false, updatable = false,
			foreignKey = @ForeignKey(name = "fk_main_category_translation_main_category"))
	private MainCategory mainCategory;

	@Column(name = "name", nullable = false, length = 255)
	private String name;

	@Column(name = "description", length = 1000)
	private String description;

	protected MainCategoryTranslation() {
	}

	public MainCategoryTranslation(MainCategory mainCategory, String localeCode, String name, String description) {
		this.mainCategory = mainCategory;
		this.setLocaleCode(localeCode);
		this.name = name;
		this.description = description;
	}

	public MainCategory getMainCategory() {
		return mainCategory;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
