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
 * Third level in the 3-level category hierarchy. Belongs to a SubType.
 * Extends BaseEntity for soft-delete and audit columns.
 */
@Entity
@Table(name = "inner_type")
public class InnerType extends BaseEntity {

	@ManyToOne(optional = false)
	@JoinColumn(name = "sub_type_id", nullable = false, updatable = false,
			foreignKey = @ForeignKey(name = "fk_inner_type_sub_type"))
	private SubType subType;

	@Column(name = "name_placeholder", nullable = false, length = 255)
	private String namePlaceholder;

	@OneToMany(mappedBy = "innerType", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<InnerTypeTranslation> translations = new ArrayList<>();

	protected InnerType() {
	}

	public InnerType(SubType subType, String namePlaceholder) {
		this.subType = subType;
		this.namePlaceholder = namePlaceholder;
	}

	public SubType getSubType() {
		return subType;
	}

	public String getNamePlaceholder() {
		return namePlaceholder;
	}

	public void setNamePlaceholder(String namePlaceholder) {
		this.namePlaceholder = namePlaceholder;
	}

	public List<InnerTypeTranslation> getTranslations() {
		return List.copyOf(translations);
	}

	public void addTranslation(InnerTypeTranslation translation) {
		translations.add(translation);
	}
}
