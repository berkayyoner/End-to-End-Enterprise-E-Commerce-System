package com.berkay.order_service.coupon.dto;

import com.berkay.order_service.coupon.entity.Coupon;
import java.math.BigDecimal;

/**
 * Response containing an active coupon.
 */
public record CouponResponse(
    String code,
    BigDecimal discountPercentage
) {
    public static CouponResponse from(Coupon coupon) {
        return new CouponResponse(
            coupon.getCode(),
            coupon.getDiscountPercentage()
        );
    }
}
