package com.schening.xinghuan.shop.goods.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * @author shenchen
 * @version 1.0
 * @date 2023/3/22 16:46
 */
@Data
public class TradeGoods implements Serializable {

    private Long goodsId;

    private String goodsName;

    private Integer goodsNumber;

    private BigDecimal goodsPrice;

    private String goodsDesc;

    private Date addTime;

}
