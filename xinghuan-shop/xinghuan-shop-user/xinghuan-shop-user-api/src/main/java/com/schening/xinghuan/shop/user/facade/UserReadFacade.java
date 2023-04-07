package com.schening.xinghuan.shop.user.facade;

import com.schening.xinghuan.shop.user.model.TradeUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author shenchen
 * @version 1.0
 * @date 2023/3/22 14:50
 */
@FeignClient(name = "shop-user", contextId = "shop-user-read")
public interface UserReadFacade {

    /**
     * 查询用户详情
     *
     * @param userId 用户ID
     * @return 用户详情
     */
    @GetMapping("/user/findByUserId")
    TradeUser findUserByUserId(@RequestParam(value = "userId") Long userId);

}
