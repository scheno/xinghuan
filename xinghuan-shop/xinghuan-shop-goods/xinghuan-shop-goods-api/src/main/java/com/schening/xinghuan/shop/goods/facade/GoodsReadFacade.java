package com.schening.xinghuan.shop.goods.facade;

import com.schening.xinghuan.shop.goods.model.TradeGoods;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author shenchen
 * @version 1.0
 * @date 2023/3/22 14:50
 */
@FeignClient(name = "shop-goods", contextId = "shop-goods-read")
public interface GoodsReadFacade {

    /**
     * 根据ID查询商品对象
     * @param goodsId 商品ID
     * @return 商品详情
     */
    @GetMapping("/goods/findByGoodId")
    TradeGoods findGoodsByGoodsId(@RequestParam(value = "goodsId") Long goodsId);

}
