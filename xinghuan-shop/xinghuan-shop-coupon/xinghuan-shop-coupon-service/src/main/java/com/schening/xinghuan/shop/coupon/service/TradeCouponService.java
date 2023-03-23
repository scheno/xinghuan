package com.schening.xinghuan.shop.coupon.service;

import com.schening.xinghuan.shop.common.entity.Result;
import com.schening.xinghuan.shop.coupon.model.TradeCoupon;

/**
 * @author shenchen
 * @version 1.0
 * @date 2023/3/23 15:05
 */
public interface TradeCouponService {

    /**
     * 查询优惠券详情
     *
     * @param couponId 优惠券ID
     * @return 优惠券详情
     */
    TradeCoupon findCouponByCouponId(Long couponId);

    /**
     * 更新优惠券状态
     *
     * @param coupon 优惠券信息
     * @return 更新结果
     */
    Result updateCouponStatus(TradeCoupon coupon);

}
