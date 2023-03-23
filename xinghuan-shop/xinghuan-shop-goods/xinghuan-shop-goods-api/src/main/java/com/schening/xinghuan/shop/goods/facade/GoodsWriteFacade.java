package com.schening.xinghuan.shop.goods.facade;

import com.schening.xinghuan.shop.common.entity.Result;
import com.schening.xinghuan.shop.goods.model.TradeGoodsNumberLog;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @author shenchen
 * @version 1.0
 * @date 2023/3/22 14:50
 */
@FeignClient(name = "shop-goods", contextId = "shop-goods-write")
public interface GoodsWriteFacade {

    /**
     * 扣减库存
     *
     * @param goodsNumberLog 扣减记录
     * @return 返回结果
     */
    @PostMapping("/goods/reduceNum")
    Result reduceGoodsNum(TradeGoodsNumberLog goodsNumberLog);

}
