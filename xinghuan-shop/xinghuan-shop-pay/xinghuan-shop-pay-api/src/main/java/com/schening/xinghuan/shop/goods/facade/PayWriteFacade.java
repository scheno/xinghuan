package com.schening.xinghuan.shop.goods.facade;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.schening.xinghuan.shop.common.entity.Result;
import com.schening.xinghuan.shop.goods.model.TradePay;

/**
 * @author shenchen
 * @version 1.0
 * @date 2023/3/22 14:50
 */
@FeignClient(name = "shop-pay", contextId = "shop-pay-write")
public interface PayWriteFacade {

    /**
     * 发起支付
     * 
     * @param pay
     * @return
     */
    @RequestMapping("/createPayment")
    public Result createPayment(@RequestBody TradePay pay);

    /**
     * 支付回调
     * 
     * @param pay
     * @return
     * @throws Exception
     */
    @RequestMapping("/callBackPayment")
    public Result callBackPayment(@RequestBody TradePay pay) throws Exception;

}
