package com.berkay.order_service.coupon.service;

import com.berkay.order_service.coupon.dto.CouponResponse;
import com.berkay.order_service.coupon.entity.Coupon;
import com.berkay.order_service.coupon.repository.CouponRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for coupon operations.
 */
@Service
@Transactional
public class CouponService {

    private final CouponRepository couponRepository;

    public CouponService(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    /**
     * Get all currently active coupons (public, no authentication required).
     * Returns coupons that are not soft-deleted and have isActive = true.
     */
    public List<CouponResponse> getActiveCoupons() {
        List<Coupon> coupons = couponRepository.findAllByDeletedFalseAndIsActiveTrue();
        return coupons.stream()
            .map(CouponResponse::from)
            .toList();
    }
}
