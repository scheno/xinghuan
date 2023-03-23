package com.schening.xinghuan.shop.goods.model;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * @author shenchen
 * @version 1.0
 * @date 2023/3/23 13:42
 */
@Data
public class TradeGoodsNumberLog extends TradeGoodsNumberLogKey implements Serializable {

    private Integer goodsNumber;

    private Date logTime;

}
