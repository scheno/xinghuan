package com.schening.xinghuan.shop.goods.model;

import java.io.Serializable;
import lombok.Data;

/**
 * @author shenchen
 * @version 1.0
 * @date 2023/3/23 13:43
 */
@Data
public class TradeGoodsNumberLogKey implements Serializable {

    private Long goodsId;

    private Long orderId;

}
