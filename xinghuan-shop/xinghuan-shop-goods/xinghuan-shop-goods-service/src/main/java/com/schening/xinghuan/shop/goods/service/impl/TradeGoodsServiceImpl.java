package com.schening.xinghuan.shop.goods.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.schening.xinghuan.shop.common.constant.ShopCode;
import com.schening.xinghuan.shop.common.entity.Result;
import com.schening.xinghuan.shop.common.exception.CastException;
import com.schening.xinghuan.shop.goods.converter.TradeGoodsConverter;
import com.schening.xinghuan.shop.goods.converter.TradeGoodsNumberLogConverter;
import com.schening.xinghuan.shop.goods.model.TradeGoods;
import com.schening.xinghuan.shop.goods.model.TradeGoodsNumberLog;
import com.schening.xinghuan.shop.goods.po.TradeGoodsPO;
import com.schening.xinghuan.shop.goods.repository.TradeGoodsNumberLogRepository;
import com.schening.xinghuan.shop.goods.repository.TradeGoodsRepository;
import com.schening.xinghuan.shop.goods.service.TradeGoodsService;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author shenchen
 * @version 1.0
 * @date 2023/3/22 17:02
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TradeGoodsServiceImpl implements TradeGoodsService {

    private final TradeGoodsRepository tradeGoodsRepository;

    private final TradeGoodsNumberLogRepository tradeGoodsNumberLogRepository;

    @Override
    public TradeGoods findGoodsByGoodsId(Long goodsId) {
        LambdaQueryWrapper<TradeGoodsPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TradeGoodsPO::getGoodsId, goodsId);
        return TradeGoodsConverter.INSTANCE.convert(tradeGoodsRepository.selectOne(queryWrapper));
    }

    @Override
    public Result reduceGoodsNum(TradeGoodsNumberLog goodsNumberLog) {
        // 参数校验
        if (goodsNumberLog == null ||
                goodsNumberLog.getGoodsNumber() == null ||
                goodsNumberLog.getOrderId() == null ||
                goodsNumberLog.getGoodsNumber() == null ||
                goodsNumberLog.getGoodsNumber().intValue() <= 0) {
            CastException.cast(ShopCode.SHOP_REQUEST_PARAMETER_VALID);
        }

        TradeGoods goods = findGoodsByGoodsId(goodsNumberLog.getGoodsId());
        if (goods.getGoodsNumber() < goodsNumberLog.getGoodsNumber()) {
            //库存不足
            CastException.cast(ShopCode.SHOP_GOODS_NUM_NOT_ENOUGH);
        }
        //减库存
        goods.setGoodsNumber(goods.getGoodsNumber() - goodsNumberLog.getGoodsNumber());
        LambdaQueryWrapper<TradeGoodsPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TradeGoodsPO::getGoodsId, goodsNumberLog.getGoodsId());
        tradeGoodsRepository.update(TradeGoodsConverter.INSTANCE.convert(goods), queryWrapper);

        //记录库存操作日志
        goodsNumberLog.setGoodsNumber(-(goodsNumberLog.getGoodsNumber()));
        goodsNumberLog.setLogTime(new Date());
        tradeGoodsNumberLogRepository.insert(TradeGoodsNumberLogConverter.INSTANCE.convert(goodsNumberLog));

        return new Result(ShopCode.SHOP_SUCCESS.getSuccess(), ShopCode.SHOP_SUCCESS.getMessage());
    }

}
