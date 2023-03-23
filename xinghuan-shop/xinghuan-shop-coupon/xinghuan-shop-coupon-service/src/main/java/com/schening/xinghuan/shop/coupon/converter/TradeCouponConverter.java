package com.schening.xinghuan.shop.coupon.converter;

import com.schening.xinghuan.shop.coupon.model.TradeCoupon;
import com.schening.xinghuan.shop.coupon.po.TradeCouponPO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author shenchen
 * @version 1.0
 * @date 2023/3/23 15:08
 */
@Mapper
public interface TradeCouponConverter {

    TradeCouponConverter INSTANCE = Mappers.getMapper(TradeCouponConverter.class);

    TradeCouponPO convert(TradeCoupon tradeCoupon);

    TradeCoupon convert(TradeCouponPO tradeCouponPO);
}
