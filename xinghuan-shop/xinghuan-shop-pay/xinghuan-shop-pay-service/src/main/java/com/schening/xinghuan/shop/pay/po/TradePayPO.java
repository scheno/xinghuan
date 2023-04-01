package com.schening.xinghuan.shop.pay.po;

import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import lombok.Data;

/**
 * @author shenchen
 * @version 1.0
 * @date 2023/3/23 17:50
 */
@Data
@TableName("trade_pay")
public class TradePayPO {

    private Long payId;

    private Long orderId;

    private BigDecimal payAmount;

    private Integer isPaid;

}
