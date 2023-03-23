package com.schening.xinghuan.shop.goods.facade;

import com.schening.xinghuan.shop.goods.model.TradeGoods;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author shenchen
 * @version 1.0
 * @date 2023/3/22 14:50
 */
@FeignClient
public interface GoodsReadFacade {

    /**
     * 根据ID查询商品对象
     * @param goodsId 商品ID
     * @return 商品详情
     */
    TradeGoods findGoodsByGoodsId(Long goodsId);

}
