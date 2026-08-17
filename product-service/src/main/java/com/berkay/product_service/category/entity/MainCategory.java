package com.berkay.product_service.category.entity;

import com.berkay.common.entity.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * Main category in the 3-level hierarchy. Extends BaseEntity for soft-delete
 * and audit columns. Translations (name, description) are in a separate table.
 */
@Entity
@Table(name = "main_category")
public class MainCategory extends BaseEntity {

	@Column(name = "name_placeholder", nullable = false, length = 255)
	private String namePlaceholder;

	@OneToMany(mappedBy = "mainCategory", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<MainCategoryTranslation> translations = new ArrayList<>();

	@OneToMany(mappedBy = "mainCategory", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<SubType> subTypes = new ArrayList<>();

	protected MainCategory() {
	}

	public MainCategory(String namePlaceholder) {
		this.namePlaceholder = namePlaceholder;
	}

	public String getNamePlaceholder() {
		return namePlaceholder;
	}

	public void setNamePlaceholder(String namePlaceholder) {
		this.namePlaceholder = namePlaceholder;
	}

	public List<MainCategoryTranslation> getTranslations() {
		return List.copyOf(translations);
	}

	public void addTranslation(MainCategoryTranslation translation) {
		translations.add(translation);
	}

	public List<SubType> getSubTypes() {
		return List.copyOf(subTypes);
	}

	public void addSubType(SubType subType) {
		subTypes.add(subType);
	}

	public void removeSubType(SubType subType) {
		subTypes.remove(subType);
	}
}
