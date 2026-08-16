package com.berkay.product_service.category.entity;

import com.berkay.common.entity.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * Second level in the 3-level category hierarchy. Belongs to a MainCategory.
 * Extends BaseEntity for soft-delete and audit columns.
 */
@Entity
@Table(name = "sub_type")
public class SubType extends BaseEntity {

	@ManyToOne(optional = false)
	@JoinColumn(name = "main_category_id", nullable = false, updatable = false,
			foreignKey = @ForeignKey(name = "fk_sub_type_main_category"))
	private MainCategory mainCategory;

	@Column(name = "name_placeholder", nullable = false, length = 255)
	private String namePlaceholder;

	@OneToMany(mappedBy = "subType", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<SubTypeTranslation> translations = new ArrayList<>();

	@OneToMany(mappedBy = "subType", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<InnerType> innerTypes = new ArrayList<>();

	protected SubType() {
	}

	public SubType(MainCategory mainCategory, String namePlaceholder) {
		this.mainCategory = mainCategory;
		this.namePlaceholder = namePlaceholder;
	}

	public MainCategory getMainCategory() {
		return mainCategory;
	}

	public String getNamePlaceholder() {
		return namePlaceholder;
	}

	public void setNamePlaceholder(String namePlaceholder) {
		this.namePlaceholder = namePlaceholder;
	}

	public List<SubTypeTranslation> getTranslations() {
		return List.copyOf(translations);
	}

	public void addTranslation(SubTypeTranslation translation) {
		translations.add(translation);
	}

	public List<InnerType> getInnerTypes() {
		return List.copyOf(innerTypes);
	}

	public void addInnerType(InnerType innerType) {
		innerTypes.add(innerType);
	}

	public void removeInnerType(InnerType innerType) {
		innerTypes.remove(innerType);
	}
}
