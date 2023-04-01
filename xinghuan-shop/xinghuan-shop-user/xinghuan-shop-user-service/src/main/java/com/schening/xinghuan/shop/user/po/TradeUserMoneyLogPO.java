package com.schening.xinghuan.shop.user.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * @author shenchen
 * @version 1.0
 * @date 2023/3/23 16:58
 */
@Data
@TableName("trade_user_money_log")
public class TradeUserMoneyLogPO {

    @TableField("user_id")
    private Long userId;

    @TableField("order_id")
    private Long orderId;

    @TableField("money_log_type")
    private Integer moneyLogType;

    @TableField("use_money")
    private BigDecimal useMoney;

    @TableField("create_time")
    private Date createTime;


}
