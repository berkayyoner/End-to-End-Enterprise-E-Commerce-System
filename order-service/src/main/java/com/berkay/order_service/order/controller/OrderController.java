package com.berkay.order_service.order.controller;

import com.berkay.order_service.order.dto.CheckoutRequest;
import com.berkay.order_service.order.dto.OrderResponse;
import com.berkay.order_service.order.service.OrderService;
import com.berkay.order_service.payment.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

	private final OrderService orderService;
	private final PaymentService paymentService;

	public OrderController(OrderService orderService, PaymentService paymentService) {
		this.orderService = orderService;
		this.paymentService = paymentService;
	}

	@PostMapping("/checkout")
	public ResponseEntity<OrderResponse> checkout(
		Authentication authentication,
		@Valid @RequestBody CheckoutRequest request
	) {
		String buyerId = authentication.getName();

		// Process payment
		paymentService.processPayment(buyerId, new com.berkay.order_service.payment.dto.PaymentRequest(
			request.payment().cardNumber(),
			request.payment().expiryMonth(),
			request.payment().expiryYear(),
			request.payment().cvv(),
			request.payment().cardHolderName(),
			request.payment().saveCard()
		));

		// Create order
		OrderResponse order = orderService.checkout(buyerId, request.couponCode());
		return ResponseEntity.status(HttpStatus.CREATED).body(order);
	}

	@GetMapping("/{orderId}")
	public ResponseEntity<OrderResponse> getOrder(
		Authentication authentication,
		@PathVariable Long orderId
	) {
		String buyerId = authentication.getName();
		OrderResponse order = orderService.getOrder(orderId, buyerId);
		return ResponseEntity.ok(order);
	}

	@GetMapping
	public ResponseEntity<Page<OrderResponse>> getMyOrders(
		Authentication authentication,
		Pageable pageable
	) {
		String buyerId = authentication.getName();
		Page<OrderResponse> orders = orderService.getMyOrders(buyerId, pageable);
		return ResponseEntity.ok(orders);
	}
}
