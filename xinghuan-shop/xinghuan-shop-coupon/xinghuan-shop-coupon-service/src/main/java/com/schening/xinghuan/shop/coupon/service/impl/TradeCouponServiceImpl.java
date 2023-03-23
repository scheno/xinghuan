package com.schening.xinghuan.shop.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.schening.xinghuan.shop.common.constant.ShopCode;
import com.schening.xinghuan.shop.common.entity.Result;
import com.schening.xinghuan.shop.common.exception.CastException;
import com.schening.xinghuan.shop.coupon.converter.TradeCouponConverter;
import com.schening.xinghuan.shop.coupon.model.TradeCoupon;
import com.schening.xinghuan.shop.coupon.po.TradeCouponPO;
import com.schening.xinghuan.shop.coupon.repository.TradeCouponRepository;
import com.schening.xinghuan.shop.coupon.service.TradeCouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author shenchen
 * @version 1.0
 * @date 2023/3/23 15:11
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TradeCouponServiceImpl implements TradeCouponService {

    private final TradeCouponRepository tradeCouponRepository;

    @Override
    public TradeCoupon findCouponByCouponId(Long couponId) {
        LambdaQueryWrapper<TradeCouponPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TradeCouponPO::getCouponId, couponId);
        return TradeCouponConverter.INSTANCE.convert(tradeCouponRepository.selectOne(queryWrapper));
    }

    @Override
    public Result updateCouponStatus(TradeCoupon coupon) {
        if(coupon==null||coupon.getCouponId()==null){
            CastException.cast(ShopCode.SHOP_REQUEST_PARAMETER_VALID);
        }
        // 更新优惠券状态
        LambdaQueryWrapper<TradeCouponPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TradeCouponPO::getCouponId, coupon.getCouponId());
        tradeCouponRepository.update(TradeCouponConverter.INSTANCE.convert(coupon), queryWrapper);
        return new Result(ShopCode.SHOP_SUCCESS.getSuccess(),ShopCode.SHOP_SUCCESS.getMessage());
    }

}
