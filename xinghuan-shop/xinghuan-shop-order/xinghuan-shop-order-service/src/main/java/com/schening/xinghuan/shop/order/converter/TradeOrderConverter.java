package com.schening.xinghuan.shop.order.converter;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.schening.xinghuan.shop.order.model.TradeOrder;
import com.schening.xinghuan.shop.order.po.TradeOrderPO;

@Mapper
public interface TradeOrderConverter {
    
    TradeOrderConverter INSTANCE = Mappers.getMapper(TradeOrderConverter.class);

    TradeOrderPO convert(TradeOrder tradeOrder);

}
