package com.schening.xinghuan.shop.goods.converter;

import com.schening.xinghuan.shop.goods.model.TradeGoods;
import com.schening.xinghuan.shop.goods.po.TradeGoodsPO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author shenchen
 * @version 1.0
 * @date 2023/3/22 17:07
 */
@Mapper
public interface TradeGoodsConverter {

    TradeGoodsConverter INSTANCE = Mappers.getMapper(TradeGoodsConverter.class);

    TradeGoods convert(TradeGoodsPO tradeGoodsPO);

    TradeGoodsPO convert(TradeGoods tradeGoods);
}
