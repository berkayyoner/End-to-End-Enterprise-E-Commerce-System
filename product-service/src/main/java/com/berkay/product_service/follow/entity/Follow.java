package com.berkay.product_service.follow.entity;

import com.berkay.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * A follow relationship: a buyer follows a seller.
 * One follow per buyer per seller. No JPA relationship to Seller (auth-service concept).
 */
@Entity
@Table(name = "follow",
		uniqueConstraints = {
			@UniqueConstraint(name = "uk_follow_buyer_seller",
					columnNames = {"buyer_id", "seller_id"})
		})
public class Follow extends BaseEntity {

	@Column(name = "buyer_id", nullable = false, updatable = false, length = 100)
	private String buyerId;

	@Column(name = "seller_id", nullable = false, updatable = false, length = 100)
	private String sellerId;

	protected Follow() {
	}

	public Follow(String buyerId, String sellerId) {
		this.buyerId = buyerId;
		this.sellerId = sellerId;
	}

	public String getBuyerId() {
		return buyerId;
	}

	public String getSellerId() {
		return sellerId;
	}
}
