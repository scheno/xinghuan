package com.schening.xinghuan.shop.pay.converter;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.schening.xinghuan.shop.goods.model.TradePay;
import com.schening.xinghuan.shop.pay.po.TradePayPO;

@Mapper
public interface TradePayConverter {

    TradePayConverter INSTANCE = Mappers.getMapper(TradePayConverter.class);
    
    TradePayPO convert(TradePay tradePay);

}
