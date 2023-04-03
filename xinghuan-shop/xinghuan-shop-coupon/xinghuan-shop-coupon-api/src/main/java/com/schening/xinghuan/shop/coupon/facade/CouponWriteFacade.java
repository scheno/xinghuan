package com.schening.xinghuan.shop.coupon.facade;

import com.schening.xinghuan.shop.common.entity.Result;
import com.schening.xinghuan.shop.coupon.model.TradeCoupon;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @author shenchen
 * @version 1.0
 * @date 2023/3/22 14:50
 */
@FeignClient(name = "shop-coupon", contextId = "shop-coupon-write")
public interface CouponWriteFacade {

    /**
     * 更新优惠券状态
     *
     * @param coupon 优惠券信息
     * @return 更新结果
     */
    @PostMapping("/coupon/updateCouponStatus")
    Result updateCouponStatus(TradeCoupon coupon);

}
