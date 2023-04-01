package com.schening.xinghuan.shop.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.schening.xinghuan.shop.common.constant.ShopCode;
import com.schening.xinghuan.shop.common.entity.Result;
import com.schening.xinghuan.shop.common.exception.CastException;
import com.schening.xinghuan.shop.user.converter.TradeUserConverter;
import com.schening.xinghuan.shop.user.converter.TradeUserMoneyLogConverter;
import com.schening.xinghuan.shop.user.model.TradeUser;
import com.schening.xinghuan.shop.user.model.TradeUserMoneyLog;
import com.schening.xinghuan.shop.user.po.TradeUserMoneyLogPO;
import com.schening.xinghuan.shop.user.po.TradeUserPO;
import com.schening.xinghuan.shop.user.repository.TradeUserMoneyLogRepository;
import com.schening.xinghuan.shop.user.repository.TradeUserRepository;
import com.schening.xinghuan.shop.user.service.TradeUserMoneyLogService;
import com.schening.xinghuan.shop.user.service.TradeUserService;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/**
 * @author shenchen
 * @version 1.0
 * @date 2023/3/23 15:11
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TradeUserMoneyLogServiceImpl implements TradeUserMoneyLogService {

    private final TradeUserService tradeUserService;

    private final TradeUserMoneyLogRepository tradeUserMoneyLogRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Result updateMoneyPaid(TradeUserMoneyLog userMoneyLog) {
        // 1.校验参数是否合法
        checkUserMoneyLog(userMoneyLog);
        // 2.判断是否存在支付记录
        List<TradeUserMoneyLog> userMoneyLogList = queryUserMoneyLog(
                userMoneyLog);

        TradeUser tradeUser = tradeUserService.findUserByUserId(userMoneyLog.getUserId());

        // 3.扣减余额...
        if (userMoneyLog.getMoneyLogType().intValue() == ShopCode.SHOP_USER_MONEY_PAID.getCode()
                .intValue()) {
            if (!CollectionUtils.isEmpty(userMoneyLogList)) {
                // 已经付款
                CastException.cast(ShopCode.SHOP_ORDER_PAY_STATUS_IS_PAY);
            }
            // 扣减余额
            tradeUser.setUserMoney(new BigDecimal(tradeUser.getUserMoney()).subtract(userMoneyLog.getUseMoney()).longValue());
            tradeUserService.updateUserMoney(tradeUser);
        }
        // 4.回退余额...
        if(userMoneyLog.getMoneyLogType().intValue()==ShopCode.SHOP_USER_MONEY_REFUND.getCode().intValue()){
            if (CollectionUtils.isEmpty(userMoneyLogList)) {
                // 如果没有支付,则不能回退余额
                CastException.cast(ShopCode.SHOP_ORDER_PAY_STATUS_NO_PAY);
            }
            for (TradeUserMoneyLog tradeUserMoneyLog : userMoneyLogList) {
                if (tradeUserMoneyLog.getMoneyLogType().intValue() == ShopCode.SHOP_USER_MONEY_REFUND.getCode()) {
                    CastException.cast(ShopCode.SHOP_USER_MONEY_REFUND_ALREADY);
                }
            }
            // 退款
            tradeUser.setUserMoney(new BigDecimal(tradeUser.getUserMoney()).add(userMoneyLog.getUseMoney()).longValue());
            tradeUserService.updateUserMoney(tradeUser);
        }
        // 5.记录订单余额使用日志
        userMoneyLog.setCreateTime(new Date());
        tradeUserMoneyLogRepository.insert(TradeUserMoneyLogConverter.INSTANCE.convert(userMoneyLog));
        return new Result(ShopCode.SHOP_SUCCESS.getSuccess(),ShopCode.SHOP_SUCCESS.getMessage());
    }

    @Override
    public List<TradeUserMoneyLog> queryUserMoneyLog(TradeUserMoneyLog userMoneyLog) {
        LambdaQueryWrapper<TradeUserMoneyLogPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TradeUserMoneyLogPO::getUserId, userMoneyLog.getUserId());
        queryWrapper.eq(TradeUserMoneyLogPO::getOrderId, userMoneyLog.getOrderId());
        return TradeUserMoneyLogConverter.INSTANCE.convert(tradeUserMoneyLogRepository.selectList(queryWrapper));
    }

    /**
     * 校验支付记录
     *
     * @param userMoneyLog 用户支付记录
     */
    private void checkUserMoneyLog(TradeUserMoneyLog userMoneyLog) {
        if (userMoneyLog == null ||
                userMoneyLog.getUserId() == null ||
                userMoneyLog.getOrderId() == null ||
                userMoneyLog.getUseMoney() == null ||
                userMoneyLog.getUseMoney().compareTo(BigDecimal.ZERO) <= 0) {
            CastException.cast(ShopCode.SHOP_REQUEST_PARAMETER_VALID);
        }
    }

}
