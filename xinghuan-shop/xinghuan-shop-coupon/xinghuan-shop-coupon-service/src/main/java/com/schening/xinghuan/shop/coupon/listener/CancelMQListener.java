package com.schening.xinghuan.shop.coupon.listener;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.schening.xinghuan.shop.common.constant.ShopCode;
import com.schening.xinghuan.shop.common.entity.MQEntity;
import com.schening.xinghuan.shop.coupon.po.TradeCouponPO;
import com.schening.xinghuan.shop.coupon.repository.TradeCouponRepository;
import java.io.UnsupportedEncodingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * @author shenchen
 * @version 1.0
 * @date 2023/4/3 17:17
 */
@Slf4j
@Component
@RequiredArgsConstructor
@RocketMQMessageListener(topic = "${mq.order.topic}", consumerGroup = "${mq.order.consumer.group.name}", messageModel = MessageModel.BROADCASTING)
public class CancelMQListener implements RocketMQListener<MessageExt> {

    private final TradeCouponRepository tradeCouponRepository;

    @Override
    public void onMessage(MessageExt message) {

        try {
            //1. 解析消息内容
            String body = new String(message.getBody(), "UTF-8");
            MQEntity mqEntity = JSON.parseObject(body, MQEntity.class);
            log.info("接收到消息");
            if(mqEntity.getCouponId()!=null){
                QueryWrapper<TradeCouponPO> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().eq(TradeCouponPO::getCouponId, mqEntity.getCouponId());
                //2. 查询优惠券信息
                TradeCouponPO coupon = tradeCouponRepository.selectOne(queryWrapper);
                //3.更改优惠券状态
                coupon.setUsedTime(null);
                coupon.setIsUsed(ShopCode.SHOP_COUPON_UNUSED.getCode());
                coupon.setOrderId(null);
                tradeCouponRepository.update(coupon, queryWrapper);
            }
            log.info("回退优惠券成功");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            log.error("回退优惠券失败");
        }

    }
}
