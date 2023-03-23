package com.schening.xinghuan.shop.coupon.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * @author shenchen
 * @version 1.0
 * @date 2023/3/23 15:06
 */
@Data
@TableName("trade_coupon")
public class TradeCouponPO {

    @TableField("coupon_id")
    private Long couponId;

    @TableField("coupon_price")
    private BigDecimal couponPrice;

    @TableField("user_id")
    private Long userId;

    @TableField("order_id")
    private Long orderId;

    @TableField("is_used")
    private Integer isUsed;

    @TableField("used_time")
    private Date usedTime;

}
