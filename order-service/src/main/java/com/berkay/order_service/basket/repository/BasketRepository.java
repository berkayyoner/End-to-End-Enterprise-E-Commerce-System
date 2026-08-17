package com.berkay.order_service.basket.repository;

import com.berkay.order_service.basket.entity.Basket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BasketRepository extends JpaRepository<Basket, Long> {
	Optional<Basket> findByBuyerIdAndDeletedFalse(String buyerId);
}
