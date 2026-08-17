package com.berkay.product_service.campaign.entity;

import com.berkay.common.entity.BaseEntity;
import com.berkay.product_service.product.entity.Product;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * Join entity representing the association between a Campaign and a Product.
 * This implements the many-to-many relationship following the repository's entity-relationship conventions.
 */
@Entity
@Table(name = "campaign_product",
		uniqueConstraints = {
			@UniqueConstraint(name = "uk_campaign_product", columnNames = {"campaign_id", "product_id"})
		})
public class CampaignProduct extends BaseEntity {

	@ManyToOne(optional = false)
	@JoinColumn(name = "campaign_id", nullable = false, updatable = false,
			foreignKey = @ForeignKey(name = "fk_campaign_product_campaign"))
	private Campaign campaign;

	@ManyToOne(optional = false)
	@JoinColumn(name = "product_id", nullable = false, updatable = false,
			foreignKey = @ForeignKey(name = "fk_campaign_product_product"))
	private Product product;

	protected CampaignProduct() {
	}

	public CampaignProduct(Campaign campaign, Product product) {
		this.campaign = campaign;
		this.product = product;
	}

	public Campaign getCampaign() {
		return campaign;
	}

	public Product getProduct() {
		return product;
	}
}
