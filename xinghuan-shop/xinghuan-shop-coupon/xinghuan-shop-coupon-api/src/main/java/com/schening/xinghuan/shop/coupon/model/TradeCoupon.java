package com.schening.xinghuan.shop.coupon.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * @author shenchen
 * @version 1.0
 * @date 2023/3/23 15:03
 */
@Data
public class TradeCoupon implements Serializable {

    private Long couponId;

    private BigDecimal couponPrice;

    private Long userId;

    private Long orderId;

    private Integer isUsed;

    private Date usedTime;

}
