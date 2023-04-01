package com.schening.xinghuan.shop.user.facade;

import com.schening.xinghuan.shop.common.entity.Result;
import com.schening.xinghuan.shop.user.model.TradeUserMoneyLog;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @author shenchen
 * @version 1.0
 * @date 2023/3/22 14:50
 */
@FeignClient(name = "shop-user", contextId = "shop-user-write")
public interface UserWriteFacade {

    /**
     * 更新用户支付金额
     *
     * @param userMoneyLog 支付记录
     * @return 返回结果
     */
    @PostMapping("/user/updateMoneyPaid")
    Result updateMoneyPaid(TradeUserMoneyLog userMoneyLog);

}
