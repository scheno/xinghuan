package com.schening.xinghuan.shop.goods.po;

import lombok.Data;

/**
 * @author shenchen
 * @version 1.0
 * @date 2023/4/3 17:55
 */
@Data
public class TradeMQConsumerLogKey {

    private String groupName;

    private String msgTag;

    private String msgKey;

}
