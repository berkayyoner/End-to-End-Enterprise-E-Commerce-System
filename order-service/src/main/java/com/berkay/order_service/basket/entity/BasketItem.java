package com.berkay.order_service.basket.entity;

import com.berkay.common.entity.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "basket_item")
public class BasketItem extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "basket_id", nullable = false)
	private Basket basket;

	@Column(name = "product_id", nullable = false)
	private Long productId;

	@Column(name = "quantity", nullable = false)
	private Integer quantity;

	public BasketItem() {}

	public BasketItem(Basket basket, Long productId, Integer quantity) {
		this.basket = basket;
		this.productId = productId;
		this.quantity = quantity;
	}

	public Basket getBasket() {
		return basket;
	}

	public void setBasket(Basket basket) {
		this.basket = basket;
	}

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
}
