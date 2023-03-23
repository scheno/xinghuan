package com.schening.xinghuan.shop.goods.service;

import com.schening.xinghuan.shop.common.entity.Result;
import com.schening.xinghuan.shop.goods.model.TradeGoods;
import com.schening.xinghuan.shop.goods.model.TradeGoodsNumberLog;

/**
 * @author shenchen
 * @version 1.0
 * @date 2023/3/22 17:00
 */
public interface TradeGoodsService {

    /**
     * 查询商品详情
     *
     * @param goodsId 商品ID
     * @return 商品详情
     */
    TradeGoods findGoodsByGoodsId(Long goodsId);

    /**
     * 扣减库存
     *
     * @param goodsNumberLog 扣减记录
     * @return 返回结果
     */
    Result reduceGoodsNum(TradeGoodsNumberLog goodsNumberLog);
}
