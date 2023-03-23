package com.schening.xinghuan.shop.goods.facade;

import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author shenchen
 * @version 1.0
 * @date 2023/3/22 14:50
 */
@FeignClient(name = "shop-pay", contextId = "shop-pay-write")
public interface PayWriteFacade {

}
