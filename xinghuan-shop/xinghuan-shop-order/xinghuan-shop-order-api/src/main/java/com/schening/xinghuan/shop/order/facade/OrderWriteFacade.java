package com.schening.xinghuan.shop.order.facade;

import com.schening.xinghuan.shop.common.entity.Result;
import com.schening.xinghuan.shop.order.request.TradeOrderRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @author shenchen
 * @version 1.0
 * @date 2023/3/22 14:50
 */
@FeignClient(name = "shop-order", contextId = "shop-order-write")
public interface OrderWriteFacade {

    /**
     * 确认订单
     *
     * @param request
     * @return
     */
    @PostMapping("/order/confirm")
    Result confirmOrder(TradeOrderRequest request);

}
