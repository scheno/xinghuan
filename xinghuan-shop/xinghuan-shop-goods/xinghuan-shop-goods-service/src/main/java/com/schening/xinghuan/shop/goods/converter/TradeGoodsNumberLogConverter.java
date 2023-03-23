package com.schening.xinghuan.shop.goods.converter;

import com.schening.xinghuan.shop.goods.model.TradeGoodsNumberLog;
import com.schening.xinghuan.shop.goods.po.TradeGoodsNumberLogPO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author shenchen
 * @version 1.0
 * @date 2023/3/22 17:07
 */
@Mapper
public interface TradeGoodsNumberLogConverter {

    TradeGoodsNumberLogConverter INSTANCE = Mappers.getMapper(TradeGoodsNumberLogConverter.class);

    TradeGoodsNumberLog convert(TradeGoodsNumberLogPO tradeGoodsNumberLogPO);

    TradeGoodsNumberLogPO convert(TradeGoodsNumberLog tradeGoodsNumberLog);
}
