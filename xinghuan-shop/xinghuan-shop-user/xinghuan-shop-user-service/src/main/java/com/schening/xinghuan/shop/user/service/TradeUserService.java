package com.schening.xinghuan.shop.user.service;

import com.schening.xinghuan.shop.common.entity.Result;
import com.schening.xinghuan.shop.user.model.TradeUser;

/**
 * @author shenchen
 * @version 1.0
 * @date 2023/3/23 15:05
 */
public interface TradeUserService {

    /**
     * 查询用户详情
     *
     * @param userId 用户ID
     * @return 用户详情
     */
    TradeUser findUserByUserId(Long userId);

    /**
     * 更新用户信息
     *
     * @param tradeUser 用户信息
     * @return 更新结果
     */
    void updateUserMoney(TradeUser tradeUser);

}
