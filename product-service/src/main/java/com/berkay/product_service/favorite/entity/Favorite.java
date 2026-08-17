package com.berkay.product_service.favorite.entity;

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
 * A favorite bookmark of a product by a buyer.
 * One favorite per buyer per product. Buyer can add/remove favorites at any time.
 */
@Entity
@Table(name = "favorite",
		uniqueConstraints = {
			@UniqueConstraint(name = "uk_favorite_buyer_product",
					columnNames = {"buyer_id", "product_id"})
		})
public class Favorite extends BaseEntity {

	@ManyToOne(optional = false)
	@JoinColumn(name = "product_id", nullable = false, updatable = false,
			foreignKey = @ForeignKey(name = "fk_favorite_product"))
	private Product product;

	@Column(name = "buyer_id", nullable = false, updatable = false, length = 100)
	private String buyerId;

	protected Favorite() {
	}

	public Favorite(Product product, String buyerId) {
		this.product = product;
		this.buyerId = buyerId;
	}

	public Product getProduct() {
		return product;
	}

	public String getBuyerId() {
		return buyerId;
	}
}
