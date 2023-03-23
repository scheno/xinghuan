package com.schening.xinghuan.shop.goods.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * @author shenchen
 * @version 1.0
 * @date 2023/3/22 16:57
 */
@Data
@TableName("trade_goods")
public class TradeGoodsPO {

    @TableField("goods_id")
    private Long goodsId;

    @TableField("goods_name")
    private String goodsName;

    @TableField("goods_number")
    private Integer goodsNumber;

    @TableField("goods_price")
    private BigDecimal goodsPrice;

    @TableField("goods_desc")
    private String goodsDesc;

    @TableField("add_time")
    private Date addTime;

}
