package com.schening.xinghuan.shop.goods.po;

import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author shenchen
 * @version 1.0
 * @date 2023/4/3 17:54
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("trade_mq_consumer_log")
public class TradeMQConsumerLogPO extends TradeMQConsumerLogKey implements Serializable {
    private String msgId;

    private String msgBody;

    private Integer consumerStatus;

    private Integer consumerTimes;

    private Date consumerTimestamp;

    private String remark;

}
