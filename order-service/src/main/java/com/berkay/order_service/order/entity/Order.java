package com.berkay.order_service.order.entity;

import com.berkay.common.entity.BaseEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order extends BaseEntity {

	@Column(name = "buyer_id", nullable = false)
	private String buyerId;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 20)
	private OrderStatus status = OrderStatus.PENDING;

	@Column(name = "total_amount", nullable = false)
	private BigDecimal totalAmount;

	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<OrderItem> items = new ArrayList<>();

	public Order() {}

	public Order(String buyerId, BigDecimal totalAmount) {
		this.buyerId = buyerId;
		this.totalAmount = totalAmount;
		this.status = OrderStatus.PENDING;
	}

	public String getBuyerId() {
		return buyerId;
	}

	public void setBuyerId(String buyerId) {
		this.buyerId = buyerId;
	}

	public OrderStatus getStatus() {
		return status;
	}

	public void setStatus(OrderStatus status) {
		this.status = status;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public List<OrderItem> getItems() {
		return items;
	}

	public void setItems(List<OrderItem> items) {
		this.items = items;
	}

	public void addItem(OrderItem item) {
		item.setOrder(this);
		this.items.add(item);
	}

	public void removeItem(OrderItem item) {
		this.items.remove(item);
		item.setOrder(null);
	}
}
