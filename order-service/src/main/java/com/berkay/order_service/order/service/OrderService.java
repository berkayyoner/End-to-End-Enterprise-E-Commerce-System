package com.berkay.order_service.order.service;

import com.berkay.order_service.basket.entity.Basket;
import com.berkay.order_service.basket.service.BasketService;
import com.berkay.order_service.coupon.repository.CouponRepository;
import com.berkay.order_service.exception.BasketNotFoundException;
import com.berkay.order_service.exception.InvalidCouponException;
import com.berkay.order_service.exception.OrderNotFoundException;
import com.berkay.order_service.exception.OrderOwnershipException;
import com.berkay.order_service.order.dto.OrderItemResponse;
import com.berkay.order_service.order.dto.OrderResponse;
import com.berkay.order_service.order.entity.Order;
import com.berkay.order_service.order.entity.OrderItem;
import com.berkay.order_service.order.entity.OrderStatus;
import com.berkay.order_service.order.repository.OrderRepository;
import com.berkay.order_service.payment.client.SellerEarningsClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional
public class OrderService {

	private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

	private final OrderRepository orderRepository;
	private final BasketService basketService;
	private final CouponRepository couponRepository;
	private final SellerEarningsClient sellerEarningsClient;

	public OrderService(
		OrderRepository orderRepository,
		BasketService basketService,
		CouponRepository couponRepository,
		SellerEarningsClient sellerEarningsClient
	) {
		this.orderRepository = orderRepository;
		this.basketService = basketService;
		this.couponRepository = couponRepository;
		this.sellerEarningsClient = sellerEarningsClient;
	}

	public OrderResponse checkout(String buyerId, String couponCode) {
		Basket basket = basketService.getOrCreateBasket(buyerId);

		if (basket.getItems().isEmpty()) {
			throw new BasketNotFoundException("Basket is empty");
		}

		// Calculate total
		BigDecimal totalAmount = basket.getItems().stream()
			.map(item -> BigDecimal.valueOf(item.getQuantity()))
			.reduce(BigDecimal.ZERO, BigDecimal::add);

		// Apply coupon if provided
		if (couponCode != null && !couponCode.isEmpty()) {
			BigDecimal discountAmount = applyCoupon(couponCode, totalAmount);
			totalAmount = totalAmount.subtract(discountAmount);
		}

		// Create order
		Order order = new Order(buyerId, totalAmount);
		order.setStatus(OrderStatus.PAID);

		// Add items to order (for now using placeholder seller info - real products would come from product-service)
		for (var basketItem : basket.getItems()) {
			// In a real scenario, we'd fetch product details from product-service
			// For now, using placeholder values
			OrderItem orderItem = new OrderItem(
				order,
				basketItem.getProductId(),
				"unknown-seller", // This should be fetched from product-service
				basketItem.getQuantity(),
				BigDecimal.ONE  // This should be fetched from product-service
			);
			order.addItem(orderItem);
		}

		Order savedOrder = orderRepository.save(order);

		// Best-effort: credit seller earnings for each item
		for (var item : savedOrder.getItems()) {
			try {
				BigDecimal itemTotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
				sellerEarningsClient.creditEarnings(item.getSellerId(), itemTotal);
			} catch (Exception e) {
				logger.warn("Failed to credit earnings for seller {}: {}", item.getSellerId(), e.getMessage());
				// Don't fail the order if seller earnings update fails
			}
		}

		// Clear basket after successful checkout
		basketService.clearBasket(buyerId);

		return mapToResponse(savedOrder);
	}

	private BigDecimal applyCoupon(String couponCode, BigDecimal totalAmount) {
		var coupon = couponRepository.findByCodeAndDeletedFalseAndIsActiveTrue(couponCode)
			.orElseThrow(() -> new InvalidCouponException("Invalid or expired coupon: " + couponCode));

		return totalAmount.multiply(coupon.getDiscountPercentage()).divide(BigDecimal.valueOf(100));
	}

	public OrderResponse getOrder(Long orderId, String buyerId) {
		Order order = orderRepository.findById(orderId)
			.orElseThrow(() -> new OrderNotFoundException("Order not found: " + orderId));

		if (!order.getBuyerId().equals(buyerId)) {
			throw new OrderOwnershipException("You do not have access to this order");
		}

		return mapToResponse(order);
	}

	public Page<OrderResponse> getMyOrders(String buyerId, Pageable pageable) {
		return orderRepository.findByBuyerIdAndDeletedFalse(buyerId, pageable)
			.map(this::mapToResponse);
	}

	private OrderResponse mapToResponse(Order order) {
		return new OrderResponse(
			order.getId(),
			order.getStatus(),
			order.getTotalAmount(),
			order.getItems().stream()
				.map(item -> new OrderItemResponse(
					item.getProductId(),
					item.getSellerId(),
					item.getQuantity(),
					item.getUnitPrice()
				))
				.toList(),
			order.getCreatedAt()
		);
	}
}
