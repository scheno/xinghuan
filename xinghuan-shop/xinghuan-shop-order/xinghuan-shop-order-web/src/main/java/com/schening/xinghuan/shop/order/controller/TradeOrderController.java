package com.schening.xinghuan.shop.order.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.schening.xinghuan.shop.common.entity.Result;
import com.schening.xinghuan.shop.order.model.TradeOrder;
import com.schening.xinghuan.shop.order.service.OrderService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class TradeOrderController {

    private final OrderService orderService;

    @PostMapping("/order/confirm")
    public Result confirmOrder(@RequestBody TradeOrder order){
        return orderService.confirmOrder(order);
    }

}
