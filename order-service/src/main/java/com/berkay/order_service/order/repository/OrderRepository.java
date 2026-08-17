package com.berkay.order_service.order.repository;

import com.berkay.order_service.order.entity.Order;
import com.berkay.order_service.order.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
	Page<Order> findByBuyerIdAndDeletedFalse(String buyerId, Pageable pageable);

	/**
	 * Check if a buyer has purchased a specific product with PAID status.
	 * Used by product-service to verify review eligibility.
	 */
	@Query("SELECT CASE WHEN COUNT(oi) > 0 THEN true ELSE false END FROM OrderItem oi " +
	       "JOIN oi.order o " +
	       "WHERE o.buyerId = :buyerId " +
	       "AND oi.productId = :productId " +
	       "AND o.status = :status " +
	       "AND o.deleted = false")
	boolean hasPaidOrder(@Param("buyerId") String buyerId,
	                      @Param("productId") Long productId,
	                      @Param("status") OrderStatus status);
}
