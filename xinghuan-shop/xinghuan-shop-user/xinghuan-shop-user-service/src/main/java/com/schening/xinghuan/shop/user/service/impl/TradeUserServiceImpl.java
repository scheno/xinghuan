package com.schening.xinghuan.shop.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.schening.xinghuan.shop.common.entity.Result;
import com.schening.xinghuan.shop.user.converter.TradeUserConverter;
import com.schening.xinghuan.shop.user.model.TradeUser;
import com.schening.xinghuan.shop.user.po.TradeUserPO;
import com.schening.xinghuan.shop.user.repository.TradeUserRepository;
import com.schening.xinghuan.shop.user.service.TradeUserService;
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
public class TradeUserServiceImpl implements TradeUserService {

    private final TradeUserRepository tradeUserRepository;

    @Override
    public TradeUser findUserByUserId(Long userId) {
        LambdaQueryWrapper<TradeUserPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TradeUserPO::getUserId, userId);
        return TradeUserConverter.INSTANCE.convert(tradeUserRepository.selectOne(queryWrapper));
    }

    @Override
    public void updateUserMoney(TradeUser tradeUser) {
        LambdaQueryWrapper<TradeUserPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TradeUserPO::getUserId, tradeUser.getUserId());
        tradeUserRepository.update(TradeUserConverter.INSTANCE.convert(tradeUser), queryWrapper);
    }

}
