package com.schening.xinghuan.shop.user.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * @author shenchen
 * @version 1.0
 * @date 2023/3/23 16:21
 */
@Data
public class TradeUserMoneyLog extends TradeUserMoneyLogKey implements Serializable {

    private BigDecimal useMoney;

    private Date createTime;

}
