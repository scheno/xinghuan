package com.schening.xinghuan.shop.user.model;

import java.io.Serializable;
import lombok.Data;

/**
 * @author shenchen
 * @version 1.0
 * @date 2023/3/23 16:48
 */
@Data
public class TradeUserMoneyLogKey implements Serializable {

    private Long userId;

    private Long orderId;

    private Integer moneyLogType;

}
