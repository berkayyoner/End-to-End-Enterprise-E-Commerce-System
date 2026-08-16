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
 * Translation of an InnerType's name and description for a specific locale.
 * Extends common-lib's TranslationEntity which provides locale_code and audit/soft-delete columns.
 */
@Entity
@Table(name = "inner_type_translation",
		uniqueConstraints = {
			@UniqueConstraint(name = "uk_inner_type_translation_innertype_locale",
					columnNames = {"inner_type_id", "locale_code"})
		})
public class InnerTypeTranslation extends TranslationEntity {

	@ManyToOne(optional = false)
	@JoinColumn(name = "inner_type_id", nullable = false, updatable = false,
			foreignKey = @ForeignKey(name = "fk_inner_type_translation_inner_type"))
	private InnerType innerType;

	@Column(name = "name", nullable = false, length = 255)
	private String name;

	@Column(name = "description", length = 1000)
	private String description;

	protected InnerTypeTranslation() {
	}

	public InnerTypeTranslation(InnerType innerType, String localeCode, String name, String description) {
		this.innerType = innerType;
		this.setLocaleCode(localeCode);
		this.name = name;
		this.description = description;
	}

	public InnerType getInnerType() {
		return innerType;
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
