package com.schening.xinghuan.shop.goods.controller;

import com.schening.xinghuan.shop.common.entity.Result;
import com.schening.xinghuan.shop.goods.model.TradeGoods;
import com.schening.xinghuan.shop.goods.model.TradeGoodsNumberLog;
import com.schening.xinghuan.shop.goods.service.TradeGoodsService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author shenchen
 * @version 1.0
 * @date 2023/3/22 17:13
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class TradeGoodsController {

    private final TradeGoodsService tradeGoodsService;

    @Operation(description = "查询商品详情")
    @PostMapping("/goods/findByGoodId")
    public TradeGoods findGoodsByGoodsId(@RequestParam Long goodsId) {
        return tradeGoodsService.findGoodsByGoodsId(goodsId);
    }

    @Operation(description = "扣减商品库存")
    @PostMapping("/goods/reduceNum")
    public Result reduceGoodsNum(@RequestBody TradeGoodsNumberLog tradeGoodsNumberLog) {
        return tradeGoodsService.reduceGoodsNum(tradeGoodsNumberLog);
    }


}
