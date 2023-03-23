package com.schening.xinghuan.shop.coupon.facade;

import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author shenchen
 * @version 1.0
 * @date 2023/3/22 14:50
 */
@FeignClient(name = "shop-coupon", contextId = "shop-coupon-write")
public interface CouponWriteFacade {

}
