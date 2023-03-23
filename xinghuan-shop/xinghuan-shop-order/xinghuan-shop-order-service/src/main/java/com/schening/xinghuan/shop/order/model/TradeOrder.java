package com.schening.xinghuan.shop.order.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * @author shenchen
 * @version 1.0
 * @date 2023/3/22 15:22
 */
@Data
public class TradeOrder implements Serializable {

    private Long orderId;

    private Long userId;

    private Integer orderStatus;

    private Integer payStatus;

    private Integer shippingStatus;

    private String address;

    private String consignee;

    private Long goodsId;

    private Integer goodsNumber;

    private BigDecimal goodsPrice;

    private Long goodsAmount;

    private BigDecimal shippingFee;

    private BigDecimal orderAmount;

    private Long couponId;

    private BigDecimal couponPaid;

    private BigDecimal moneyPaid;

    private BigDecimal payAmount;

    private Date addTime;

    private Date confirmTime;

    private Date payTime;

}


