package com.schening.xinghuan.shop.user.converter;

import com.schening.xinghuan.shop.user.model.TradeUserMoneyLog;
import com.schening.xinghuan.shop.user.po.TradeUserMoneyLogPO;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author shenchen
 * @version 1.0
 * @date 2023/3/23 15:08
 */
@Mapper
public interface TradeUserMoneyLogConverter {

    TradeUserMoneyLogConverter INSTANCE = Mappers.getMapper(TradeUserMoneyLogConverter.class);

    TradeUserMoneyLog convert(TradeUserMoneyLogPO userMoneyLogPO);

    TradeUserMoneyLogPO convert(TradeUserMoneyLog userMoneyLog);

    List<TradeUserMoneyLog> convert(List<TradeUserMoneyLogPO> userMoneyLogPOList);

}
