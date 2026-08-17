package com.berkay.order_service.basket.service;

import com.berkay.order_service.basket.dto.AddToBasketRequest;
import com.berkay.order_service.basket.dto.BasketResponse;
import com.berkay.order_service.basket.dto.UpdateBasketItemRequest;
import com.berkay.order_service.basket.entity.Basket;
import com.berkay.order_service.basket.entity.BasketItem;
import com.berkay.order_service.basket.repository.BasketRepository;
import com.berkay.order_service.exception.BasketNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BasketServiceTest {

	@Mock
	private BasketRepository basketRepository;

	@InjectMocks
	private BasketService basketService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testAddItemToBasket() {
		String buyerId = "buyer1";
		Basket basket = new Basket(buyerId);
		AddToBasketRequest request = new AddToBasketRequest(1L, 2);

		when(basketRepository.findByBuyerIdAndDeletedFalse(buyerId)).thenReturn(Optional.of(basket));
		when(basketRepository.save(basket)).thenReturn(basket);

		BasketResponse response = basketService.addItem(buyerId, request);

		assertNotNull(response);
		assertEquals(1, response.items().size());
		verify(basketRepository).save(basket);
	}

	@Test
	void testAddItemToNewBasket() {
		String buyerId = "buyer1";
		Basket basket = new Basket(buyerId);
		AddToBasketRequest request = new AddToBasketRequest(1L, 2);

		when(basketRepository.findByBuyerIdAndDeletedFalse(buyerId)).thenReturn(Optional.empty());
		when(basketRepository.save(any(Basket.class))).thenReturn(basket);

		BasketResponse response = basketService.addItem(buyerId, request);

		assertNotNull(response);
		assertEquals(1, response.items().size());
		verify(basketRepository, times(2)).save(any(Basket.class));
	}

	@Test
	void testUpdateItemQuantity() {
		String buyerId = "buyer1";
		Basket basket = new Basket(buyerId);
		BasketItem item = new BasketItem(basket, 1L, 2);
		basket.addItem(item);

		// Simulate ID being set by JPA after persistence
		java.lang.reflect.Field idField = null;
		try {
			idField = com.berkay.common.entity.BaseEntity.class.getDeclaredField("id");
			idField.setAccessible(true);
			idField.set(item, 1L);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		when(basketRepository.findByBuyerIdAndDeletedFalse(buyerId)).thenReturn(Optional.of(basket));
		when(basketRepository.save(basket)).thenReturn(basket);

		UpdateBasketItemRequest request = new UpdateBasketItemRequest(5);
		BasketResponse response = basketService.updateItemQuantity(buyerId, 1L, request);

		assertNotNull(response);
		assertEquals(5, item.getQuantity());
		verify(basketRepository).save(basket);
	}

	@Test
	void testRemoveItem() {
		String buyerId = "buyer1";
		Basket basket = new Basket(buyerId);
		BasketItem item = new BasketItem(basket, 1L, 2);
		basket.addItem(item);

		// Simulate ID being set by JPA after persistence
		java.lang.reflect.Field idField = null;
		try {
			idField = com.berkay.common.entity.BaseEntity.class.getDeclaredField("id");
			idField.setAccessible(true);
			idField.set(item, 1L);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		when(basketRepository.findByBuyerIdAndDeletedFalse(buyerId)).thenReturn(Optional.of(basket));
		when(basketRepository.save(basket)).thenReturn(basket);

		BasketResponse response = basketService.removeItem(buyerId, 1L);

		assertNotNull(response);
		assertEquals(0, response.items().size());
		verify(basketRepository).save(basket);
	}

	@Test
	void testRemoveItemNotFound() {
		String buyerId = "buyer1";
		Basket basket = new Basket(buyerId);

		when(basketRepository.findByBuyerIdAndDeletedFalse(buyerId)).thenReturn(Optional.of(basket));

		assertThrows(BasketNotFoundException.class, () -> basketService.removeItem(buyerId, 1L));
	}

	@Test
	void testClearBasket() {
		String buyerId = "buyer1";
		Basket basket = new Basket(buyerId);
		BasketItem item = new BasketItem(basket, 1L, 2);
		basket.addItem(item);

		when(basketRepository.findByBuyerIdAndDeletedFalse(buyerId)).thenReturn(Optional.of(basket));
		when(basketRepository.save(basket)).thenReturn(basket);

		basketService.clearBasket(buyerId);

		assertEquals(0, basket.getItems().size());
		verify(basketRepository).save(basket);
	}
}
