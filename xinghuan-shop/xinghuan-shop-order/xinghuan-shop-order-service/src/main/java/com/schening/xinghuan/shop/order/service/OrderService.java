package com.schening.xinghuan.shop.order.service;

import com.schening.xinghuan.shop.common.entity.Result;
import com.schening.xinghuan.shop.order.model.TradeOrder;

/**
 * @author shenchen
 * @version 1.0
 * @date 2023/3/22 15:14
 */
public interface OrderService {

    /**
     * 下单接口
     *
     * @param order 订单
     * @return 创建结果
     */
    Result confirmOrder(TradeOrder order);

}
