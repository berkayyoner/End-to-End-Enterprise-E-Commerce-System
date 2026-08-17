package com.berkay.order_service.order.repository;

import com.berkay.order_service.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
	Page<Order> findByBuyerIdAndDeletedFalse(String buyerId, Pageable pageable);
}
