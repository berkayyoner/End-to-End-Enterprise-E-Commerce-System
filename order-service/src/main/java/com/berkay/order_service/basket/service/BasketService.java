package com.berkay.order_service.basket.service;

import com.berkay.order_service.basket.dto.AddToBasketRequest;
import com.berkay.order_service.basket.dto.BasketResponse;
import com.berkay.order_service.basket.dto.UpdateBasketItemRequest;
import com.berkay.order_service.basket.entity.Basket;
import com.berkay.order_service.basket.entity.BasketItem;
import com.berkay.order_service.basket.repository.BasketRepository;
import com.berkay.order_service.exception.BasketNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BasketService {

	private final BasketRepository basketRepository;

	public BasketService(BasketRepository basketRepository) {
		this.basketRepository = basketRepository;
	}

	public BasketResponse getMyBasket(String buyerId) {
		Basket basket = getOrCreateBasket(buyerId);
		return mapToResponse(basket);
	}

	public BasketResponse addItem(String buyerId, AddToBasketRequest request) {
		Basket basket = getOrCreateBasket(buyerId);

		// Check if item already exists, update quantity
		BasketItem existingItem = basket.getItems().stream()
			.filter(item -> item.getProductId().equals(request.productId()))
			.findFirst()
			.orElse(null);

		if (existingItem != null) {
			existingItem.setQuantity(existingItem.getQuantity() + request.quantity());
		} else {
			BasketItem newItem = new BasketItem(basket, request.productId(), request.quantity());
			basket.addItem(newItem);
		}

		basketRepository.save(basket);
		return mapToResponse(basket);
	}

	public BasketResponse updateItemQuantity(String buyerId, Long itemId, UpdateBasketItemRequest request) {
		Basket basket = basketRepository.findByBuyerIdAndDeletedFalse(buyerId)
			.orElseThrow(() -> new BasketNotFoundException("Basket not found for buyer: " + buyerId));

		BasketItem item = basket.getItems().stream()
			.filter(i -> i.getId().equals(itemId))
			.findFirst()
			.orElseThrow(() -> new BasketNotFoundException("Item not found in basket"));

		item.setQuantity(request.quantity());
		basketRepository.save(basket);
		return mapToResponse(basket);
	}

	public BasketResponse removeItem(String buyerId, Long itemId) {
		Basket basket = basketRepository.findByBuyerIdAndDeletedFalse(buyerId)
			.orElseThrow(() -> new BasketNotFoundException("Basket not found for buyer: " + buyerId));

		BasketItem item = basket.getItems().stream()
			.filter(i -> i.getId().equals(itemId))
			.findFirst()
			.orElseThrow(() -> new BasketNotFoundException("Item not found in basket"));

		basket.removeItem(item);
		basketRepository.save(basket);
		return mapToResponse(basket);
	}

	public Basket getOrCreateBasket(String buyerId) {
		return basketRepository.findByBuyerIdAndDeletedFalse(buyerId)
			.orElseGet(() -> {
				Basket newBasket = new Basket(buyerId);
				return basketRepository.save(newBasket);
			});
	}

	public void clearBasket(String buyerId) {
		Basket basket = basketRepository.findByBuyerIdAndDeletedFalse(buyerId)
			.orElseThrow(() -> new BasketNotFoundException("Basket not found for buyer: " + buyerId));

		basket.getItems().clear();
		basketRepository.save(basket);
	}

	private BasketResponse mapToResponse(Basket basket) {
		return new BasketResponse(
			basket.getId(),
			basket.getItems().stream()
				.map(item -> new com.berkay.order_service.basket.dto.BasketItemResponse(
					item.getId(),
					item.getProductId(),
					item.getQuantity()
				))
				.toList()
		);
	}
}
