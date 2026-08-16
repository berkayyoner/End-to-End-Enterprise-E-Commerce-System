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
 * Translation of a SubType's name and description for a specific locale.
 * Extends common-lib's TranslationEntity which provides locale_code and audit/soft-delete columns.
 */
@Entity
@Table(name = "sub_type_translation",
		uniqueConstraints = {
			@UniqueConstraint(name = "uk_sub_type_translation_subtype_locale",
					columnNames = {"sub_type_id", "locale_code"})
		})
public class SubTypeTranslation extends TranslationEntity {

	@ManyToOne(optional = false)
	@JoinColumn(name = "sub_type_id", nullable = false, updatable = false,
			foreignKey = @ForeignKey(name = "fk_sub_type_translation_sub_type"))
	private SubType subType;

	@Column(name = "name", nullable = false, length = 255)
	private String name;

	@Column(name = "description", length = 1000)
	private String description;

	protected SubTypeTranslation() {
	}

	public SubTypeTranslation(SubType subType, String localeCode, String name, String description) {
		this.subType = subType;
		this.setLocaleCode(localeCode);
		this.name = name;
		this.description = description;
	}

	public SubType getSubType() {
		return subType;
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
