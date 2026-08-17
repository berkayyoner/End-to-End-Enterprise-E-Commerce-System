package com.berkay.order_service.basket.entity;

import com.berkay.common.entity.BaseEntity;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "basket", uniqueConstraints = @UniqueConstraint(columnNames = "buyer_id", name = "uk_basket_buyer_id"))
public class Basket extends BaseEntity {

	@Column(name = "buyer_id", nullable = false)
	private String buyerId;

	@OneToMany(mappedBy = "basket", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<BasketItem> items = new ArrayList<>();

	public Basket() {}

	public Basket(String buyerId) {
		this.buyerId = buyerId;
	}

	public String getBuyerId() {
		return buyerId;
	}

	public void setBuyerId(String buyerId) {
		this.buyerId = buyerId;
	}

	public List<BasketItem> getItems() {
		return items;
	}

	public void setItems(List<BasketItem> items) {
		this.items = items;
	}

	public void addItem(BasketItem item) {
		item.setBasket(this);
		this.items.add(item);
	}

	public void removeItem(BasketItem item) {
		this.items.remove(item);
		item.setBasket(null);
	}
}
