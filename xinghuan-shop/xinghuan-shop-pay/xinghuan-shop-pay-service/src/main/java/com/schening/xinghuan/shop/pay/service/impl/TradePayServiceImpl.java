package com.schening.xinghuan.shop.pay.service.impl;

import com.schening.xinghuan.shop.common.constant.ShopCode;
import com.schening.xinghuan.shop.common.entity.Result;
import com.schening.xinghuan.shop.common.exception.CastException;
import com.schening.xinghuan.shop.goods.model.TradePay;
import com.schening.xinghuan.shop.pay.service.TradePayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author shenchen
 * @version 1.0
 * @date 2023/3/23 18:19
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TradePayServiceImpl implements TradePayService {

    @Override
    public Result createPayment(TradePay tradePay) {
        if(tradePay==null || tradePay.getOrderId()==null){
            CastException.cast(ShopCode.SHOP_REQUEST_PARAMETER_VALID);
        }


        return null;
    }

    @Override
    public Result callbackPayment(TradePay pay) {
        return null;
    }

}
