package com.berkay.order_service.coupon.entity;

import com.berkay.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "coupon")
public class Coupon extends BaseEntity {

	@Column(name = "code", nullable = false, unique = true, length = 50)
	private String code;

	@Column(name = "discount_percentage", nullable = false)
	private BigDecimal discountPercentage;

	@Column(name = "is_active", nullable = false)
	private Boolean isActive = true;

	public Coupon() {}

	public Coupon(String code, BigDecimal discountPercentage) {
		this.code = code;
		this.discountPercentage = discountPercentage;
		this.isActive = true;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public BigDecimal getDiscountPercentage() {
		return discountPercentage;
	}

	public void setDiscountPercentage(BigDecimal discountPercentage) {
		this.discountPercentage = discountPercentage;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean active) {
		isActive = active;
	}
}
