package com.schening.xinghuan.shop.coupon.controller;

import com.schening.xinghuan.shop.common.entity.Result;
import com.schening.xinghuan.shop.coupon.model.TradeCoupon;
import com.schening.xinghuan.shop.coupon.service.TradeCouponService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author shenchen
 * @version 1.0
 * @date 2023/3/23 15:20
 */
@RestController
@RequiredArgsConstructor
public class TradeCouponController {

    private final TradeCouponService tradeCouponService;

    /**
     * 查询优惠券详情
     *
     * @param couponId 优惠券ID
     * @return 优惠券详情
     */
    @Operation(description = "查询优惠券详情")
    @GetMapping("/coupon/findByCouponId")
    public TradeCoupon findCouponByCouponId(@RequestParam Long couponId) {
        return tradeCouponService.findCouponByCouponId(couponId);
    }

    /**
     * 更新优惠券状态
     *
     * @param coupon 优惠券信息
     * @return 更新结果
     */
    @Operation(description = "更新优惠券状态")
    @PostMapping("/coupon/updateCouponStatus")
    public Result updateCouponStatus(TradeCoupon coupon) {
        return tradeCouponService.updateCouponStatus(coupon);
    }

}
