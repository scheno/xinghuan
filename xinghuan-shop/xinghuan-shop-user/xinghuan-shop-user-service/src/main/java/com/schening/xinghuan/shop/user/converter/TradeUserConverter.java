package com.schening.xinghuan.shop.user.converter;

import com.schening.xinghuan.shop.user.model.TradeUser;
import com.schening.xinghuan.shop.user.po.TradeUserPO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author shenchen
 * @version 1.0
 * @date 2023/3/23 15:08
 */
@Mapper
public interface TradeUserConverter {

    TradeUserConverter INSTANCE = Mappers.getMapper(TradeUserConverter.class);

    TradeUserPO convert(TradeUser tradeUser);

    TradeUser convert(TradeUserPO tradeUserPO);
}
