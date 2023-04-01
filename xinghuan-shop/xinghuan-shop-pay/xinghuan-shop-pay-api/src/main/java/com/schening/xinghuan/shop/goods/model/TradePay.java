package com.schening.xinghuan.shop.goods.model;

import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;

/**
 * @author shenchen
 * @version 1.0
 * @date 2023/3/23 17:47
 */
@Data
public class TradePay implements Serializable {

    private Long payId;

    private Long orderId;

    private BigDecimal payAmount;

    private Integer isPaid;

}
