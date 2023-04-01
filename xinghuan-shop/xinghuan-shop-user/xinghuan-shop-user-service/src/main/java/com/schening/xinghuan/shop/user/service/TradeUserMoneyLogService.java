package com.schening.xinghuan.shop.user.service;

import com.schening.xinghuan.shop.common.entity.Result;
import com.schening.xinghuan.shop.user.model.TradeUserMoneyLog;
import java.util.List;

/**
 * @author shenchen
 * @version 1.0
 * @date 2023/3/23 17:06
 */
public interface TradeUserMoneyLogService {

    /**
     * 查询用户支付记录
     *
     * @param userMoneyLog 支付记录
     * @return 支付记录
     */
    List<TradeUserMoneyLog> queryUserMoneyLog(TradeUserMoneyLog userMoneyLog);

    /**
     * 更新用户支付记录
     *
     * @param userMoneyLog 支付记录
     * @return 返回结果
     */
    Result updateMoneyPaid(TradeUserMoneyLog userMoneyLog);

}
