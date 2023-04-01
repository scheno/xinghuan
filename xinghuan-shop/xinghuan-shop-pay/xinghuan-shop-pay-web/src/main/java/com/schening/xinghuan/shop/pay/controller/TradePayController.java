package com.schening.xinghuan.shop.pay.controller;

import com.schening.xinghuan.shop.common.entity.Result;
import com.schening.xinghuan.shop.goods.model.TradePay;
import com.schening.xinghuan.shop.pay.service.TradePayService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author shenchen
 * @version 1.0
 * @date 2023/3/23 17:41
 */
@RestController
@RequiredArgsConstructor
public class TradePayController {

    private final TradePayService payService;

    @GetMapping("/createPayment")
    public Result createPayment(@RequestBody TradePay pay){
        return payService.createPayment(pay);
    }

    @PostMapping("/callBackPayment")
    public Result callBackPayment(@RequestBody TradePay pay) throws Exception {
        return payService.callbackPayment(pay);
    }

}
