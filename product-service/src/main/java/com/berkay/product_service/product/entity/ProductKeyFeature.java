package com.berkay.product_service.product.entity;

import com.berkay.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * A key feature (bullet point) for a Product.
 */
@Entity
@Table(name = "product_key_feature")
public class ProductKeyFeature extends BaseEntity {

	@ManyToOne(optional = false)
	@JoinColumn(name = "product_id", nullable = false, updatable = false,
			foreignKey = @ForeignKey(name = "fk_product_key_feature_product"))
	private Product product;

	@Column(name = "feature_text", nullable = false, length = 500)
	private String featureText;

	protected ProductKeyFeature() {
	}

	public ProductKeyFeature(Product product, String featureText) {
		this.product = product;
		this.featureText = featureText;
	}

	public Product getProduct() {
		return product;
	}

	public String getFeatureText() {
		return featureText;
	}

	public void setFeatureText(String featureText) {
		this.featureText = featureText;
	}
}
