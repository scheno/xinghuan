package com.schening.xinghuan.shop.user.controller;

import com.schening.xinghuan.shop.common.entity.Result;
import com.schening.xinghuan.shop.user.model.TradeUser;
import com.schening.xinghuan.shop.user.model.TradeUserMoneyLog;
import com.schening.xinghuan.shop.user.service.TradeUserMoneyLogService;
import com.schening.xinghuan.shop.user.service.TradeUserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author shenchen
 * @version 1.0
 * @date 2023/3/23 15:20
 */
@RestController
@RequiredArgsConstructor
public class TradeUserController {

    private final TradeUserService tradeUserService;

    private final TradeUserMoneyLogService tradeUserMoneyLogService;

    /**
     * 查询用户详情
     *
     * @param userId 用户ID
     * @return 用户详情
     */
    @Operation(description = "查询用户详情")
    @GetMapping("/user/findByUserId")
    TradeUser findUserByUserId(@RequestParam(value = "userId") Long userId) {
        return tradeUserService.findUserByUserId(userId);
    }

    /**
     * 更新用户支付金额
     *
     * @param userMoneyLog 支付记录
     * @return 返回结果
     */
    @Operation(description = "查询用户详情")
    @PostMapping("/user/updateMoneyPaid")
    Result updateMoneyPaid(TradeUserMoneyLog userMoneyLog) {
        return tradeUserMoneyLogService.updateMoneyPaid(userMoneyLog);
    }

}
