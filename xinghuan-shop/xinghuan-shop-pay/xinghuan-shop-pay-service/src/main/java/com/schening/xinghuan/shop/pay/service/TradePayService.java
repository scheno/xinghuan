package com.schening.xinghuan.shop.pay.service;

import com.schening.xinghuan.shop.common.entity.Result;
import com.schening.xinghuan.shop.goods.model.TradePay;

/**
 * @author shenchen
 * @version 1.0
 * @date 2023/3/23 17:49
 */
public interface TradePayService {

    /**
     * 发起支付
     *
     * @param tradePay 支付信息
     * @return 返回结果
     */
    Result createPayment(TradePay tradePay);

    /**
     * 支付回调
     *
     * @param tradePay 支付信息
     * @return 返回结果
     */
    Result callbackPayment(TradePay tradePay);

}
