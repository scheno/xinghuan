package com.schening.xinghuan.shop.order.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * @author shenchen
 * @version 1.0
 * @date 2023/3/22 16:19
 */
@Data
@TableName("trade_order")
public class TradeOrderPO {


    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("order_id")
    private Long orderId;

    @TableField("user_id")
    private Long userId;

    @TableField("order_status")
    private Integer orderStatus;

    @TableField("pay_status")
    private Integer payStatus;

    @TableField("shipping_status")
    private Integer shippingStatus;

    @TableField("address")
    private String address;

    @TableField("consignee")
    private String consignee;

    @TableField("goods_id")
    private Long goodsId;

    @TableField("goods_number")
    private Integer goodsNumber;

    @TableField("goods_price")
    private BigDecimal goodsPrice;

    @TableField("goods_amount")
    private Long goodsAmount;

    @TableField("shipping_fee")
    private BigDecimal shippingFee;

    @TableField("order_amount")
    private BigDecimal orderAmount;

    @TableField("coupon_id")
    private Long couponId;

    @TableField("coupon_paid")
    private BigDecimal couponPaid;

    @TableField("money_paid")
    private BigDecimal moneyPaid;

    @TableField("pay_amount")
    private BigDecimal payAmount;

    @TableField("add_time")
    private Date addTime;

    @TableField("confirm_time")
    private Date confirmTime;

    @TableField("pay_time")
    private Date payTime;

}
