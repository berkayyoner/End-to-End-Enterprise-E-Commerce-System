package com.berkay.order_service.basket.controller;

import com.berkay.order_service.basket.dto.AddToBasketRequest;
import com.berkay.order_service.basket.dto.BasketResponse;
import com.berkay.order_service.basket.dto.UpdateBasketItemRequest;
import com.berkay.order_service.basket.service.BasketService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/basket")
public class BasketController {

	private final BasketService basketService;

	public BasketController(BasketService basketService) {
		this.basketService = basketService;
	}

	@GetMapping
	public ResponseEntity<BasketResponse> getMyBasket(Authentication authentication) {
		String buyerId = authentication.getName();
		BasketResponse basket = basketService.getMyBasket(buyerId);
		return ResponseEntity.ok(basket);
	}

	@PostMapping("/items")
	public ResponseEntity<BasketResponse> addItem(
		Authentication authentication,
		@Valid @RequestBody AddToBasketRequest request
	) {
		String buyerId = authentication.getName();
		BasketResponse basket = basketService.addItem(buyerId, request);
		return ResponseEntity.status(HttpStatus.CREATED).body(basket);
	}

	@PutMapping("/items/{itemId}")
	public ResponseEntity<BasketResponse> updateItemQuantity(
		Authentication authentication,
		@PathVariable Long itemId,
		@Valid @RequestBody UpdateBasketItemRequest request
	) {
		String buyerId = authentication.getName();
		BasketResponse basket = basketService.updateItemQuantity(buyerId, itemId, request);
		return ResponseEntity.ok(basket);
	}

	@DeleteMapping("/items/{itemId}")
	public ResponseEntity<BasketResponse> removeItem(
		Authentication authentication,
		@PathVariable Long itemId
	) {
		String buyerId = authentication.getName();
		BasketResponse basket = basketService.removeItem(buyerId, itemId);
		return ResponseEntity.ok(basket);
	}
}
