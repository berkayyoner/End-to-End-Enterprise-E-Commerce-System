package com.berkay.order_service.coupon.repository;

import com.berkay.order_service.coupon.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {
	Optional<Coupon> findByCodeAndDeletedFalseAndIsActiveTrue(String code);
}
