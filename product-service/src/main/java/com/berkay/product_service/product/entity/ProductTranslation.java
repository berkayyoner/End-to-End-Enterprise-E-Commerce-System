package com.berkay.product_service.product.entity;

import com.berkay.common.i18n.TranslationEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * Translation of a Product's name and descriptions for a specific locale.
 * Extends common-lib's TranslationEntity which provides locale_code and audit/soft-delete columns.
 */
@Entity
@Table(name = "product_translation",
		uniqueConstraints = {
			@UniqueConstraint(name = "uk_product_translation_product_locale",
					columnNames = {"product_id", "locale_code"})
		})
public class ProductTranslation extends TranslationEntity {

	@ManyToOne(optional = false)
	@JoinColumn(name = "product_id", nullable = false, updatable = false,
			foreignKey = @ForeignKey(name = "fk_product_translation_product"))
	private Product product;

	@Column(name = "name", nullable = false, length = 255)
	private String name;

	@Column(name = "short_description", length = 500)
	private String shortDescription;

	@Column(name = "long_description", length = 2000)
	private String longDescription;

	protected ProductTranslation() {
	}

	public ProductTranslation(Product product, String localeCode, String name,
			String shortDescription, String longDescription) {
		this.product = product;
		this.setLocaleCode(localeCode);
		this.name = name;
		this.shortDescription = shortDescription;
		this.longDescription = longDescription;
	}

	public Product getProduct() {
		return product;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getShortDescription() {
		return shortDescription;
	}

	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}

	public String getLongDescription() {
		return longDescription;
	}

	public void setLongDescription(String longDescription) {
		this.longDescription = longDescription;
	}
}
