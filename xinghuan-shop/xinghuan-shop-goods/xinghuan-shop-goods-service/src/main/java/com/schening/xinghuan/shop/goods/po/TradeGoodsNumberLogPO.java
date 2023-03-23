package com.schening.xinghuan.shop.goods.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * @author shenchen
 * @version 1.0
 * @date 2023/3/23 13:48
 */
@Data
@TableName("trade_goods_number_log")
public class TradeGoodsNumberLogPO {

    @TableField(value = "goods_id")
    private Long goodsId;

    @TableField(value = "order_id")
    private Long orderId;

    @TableField(value = "goods_number")
    private Integer goodsNumber;

    @TableField(value = "log_time")
    private Date logTime;

}
