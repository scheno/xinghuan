package com.schening.xinghuan.shop.common.entity;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * @author shenchen
 * @version 1.0
 * @date 2023/3/21 10:50
 */
@Data
@Builder
@AllArgsConstructor
public class MQEntity {

    private Long orderId;

    private Long couponId;

    private Long userId;

    private BigDecimal userMoney;

    private Long goodsId;

    private Integer goodsNum;

}
