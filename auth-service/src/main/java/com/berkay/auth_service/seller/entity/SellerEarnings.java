package com.berkay.auth_service.seller.entity;

import com.berkay.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.math.BigDecimal;

/**
 * Seller earnings ledger - a running total of earnings per seller. Updated on order completion
 * via Phase 5's order-service calling this entity's credit() method. Phase 4.1 creates the
 * entity and domain method; Phase 5 will wire the order-completion trigger.
 */
@Entity
@Table(name = "seller_earnings", uniqueConstraints = @UniqueConstraint(name = "uk_seller_earnings_seller_id", columnNames = "seller_id"))
public class SellerEarnings extends BaseEntity {

	@Column(name = "seller_id", nullable = false)
	private Long sellerId;

	@Column(name = "total_earned", nullable = false, precision = 19, scale = 2)
	private BigDecimal totalEarned = BigDecimal.ZERO;

	protected SellerEarnings() {
	}

	public SellerEarnings(Long sellerId) {
		this.sellerId = sellerId;
		this.totalEarned = BigDecimal.ZERO;
	}

	public Long getSellerId() {
		return sellerId;
	}

	public BigDecimal getTotalEarned() {
		return totalEarned;
	}

	/**
	 * Adds earnings to this seller's total. Called by order-service on order completion
	 * (Phase 5). No withdrawal/payout logic per ANALYSIS.md scope pivot - just a running
	 * total for display in the SPA.
	 */
	public void credit(BigDecimal amount) {
		if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
			throw new IllegalArgumentException("Credit amount must be non-null and non-negative");
		}
		this.totalEarned = this.totalEarned.add(amount);
	}
}
