package com.schening.xinghuan.shop.coupon.facade;

import com.schening.xinghuan.shop.coupon.model.TradeCoupon;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author shenchen
 * @version 1.0
 * @date 2023/3/22 14:50
 */
@FeignClient(name = "shop-coupon", contextId = "shop-coupon-read")
public interface CouponReadFacade {

    /**
     * 查询优惠券详情
     *
     * @param couponId 优惠券ID
     * @return 优惠券详情
     */
    TradeCoupon findCouponByCouponId(Long couponId);

}
