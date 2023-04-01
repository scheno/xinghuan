package com.schening.xinghuan.shop.pay.po;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 
 */
@Data
@TableName("trade_mq_message")
public class TradeMQMessagePO {

    private String id;

    private String groupName;

    private String msgTopic;

    private String msgTag;

    private String msgKey;

    private String msgBody;

    private Integer msgStatus;

    private Date createTime;

}