package com.schening.xinghuan.shop.pay.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.schening.xinghuan.shop.common.constant.ShopCode;
import com.schening.xinghuan.shop.common.entity.Result;
import com.schening.xinghuan.shop.common.exception.CastException;
import com.schening.xinghuan.shop.common.utils.IDWorker;
import com.schening.xinghuan.shop.goods.model.TradePay;
import com.schening.xinghuan.shop.pay.converter.TradePayConverter;
import com.schening.xinghuan.shop.pay.po.TradeMQMessagePO;
import com.schening.xinghuan.shop.pay.po.TradePayPO;
import com.schening.xinghuan.shop.pay.repository.TradeMQMessageRepository;
import com.schening.xinghuan.shop.pay.repository.TradePayRepository;
import com.schening.xinghuan.shop.pay.service.TradePayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

/**
 * @author shenchen
 * @version 1.0
 * @date 2023/3/23 18:19
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TradePayServiceImpl implements TradePayService {

    private final IDWorker idWorker;

    private final TradePayRepository tradePayRepository;

    private final TradeMQMessageRepository tradeMQMessageRepository;

    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;

    private final RocketMQTemplate rocketMQTemplate;

    @Value("${rocketmq.producer.group}")
    private String groupName;

    @Value("${mq.topic}")
    private String topic;

    @Value("${mq.pay.tag}")
    private String tag;


    @Override
    public Result createPayment(TradePay tradePay) {
        if (tradePay == null || tradePay.getOrderId() == null) {
            CastException.cast(ShopCode.SHOP_REQUEST_PARAMETER_VALID);
        }

        // 1.判断订单支付状态
        LambdaQueryWrapper<TradePayPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TradePayPO::getOrderId, tradePay.getOrderId()).eq(TradePayPO::getIsPaid,
                ShopCode.SHOP_PAYMENT_IS_PAID.getCode());
        TradePayPO tradePayPO = tradePayRepository.selectOne(queryWrapper);
        if (tradePayPO != null) {
            CastException.cast(ShopCode.SHOP_PAYMENT_IS_PAID);
        }

        tradePayPO = TradePayConverter.INSTANCE.convert(tradePay);
        // 2.设置订单的状态为未支付
        tradePayPO.setIsPaid(ShopCode.SHOP_ORDER_PAY_STATUS_NO_PAY.getCode());

        // 3.保存支付订单
        tradePayPO.setPayId(idWorker.nextId());
        tradePayRepository.insert(tradePayPO);

        return new Result(ShopCode.SHOP_SUCCESS.getSuccess(), ShopCode.SHOP_SUCCESS.getMessage());
    }

    @Override
    public Result callbackPayment(TradePay tradePay) {
        log.info("支付回调");
        // 1. 判断用户支付状态
        if (tradePay.getIsPaid().intValue() == ShopCode.SHOP_ORDER_PAY_STATUS_IS_PAY.getCode().intValue()) {
            // 2. 更新支付订单状态为已支付
            Long payId = tradePay.getPayId();
            QueryWrapper<TradePayPO> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(TradePayPO::getPayId, payId);
            TradePayPO tradePayPO = tradePayRepository.selectOne(queryWrapper);
            // 判断支付订单是否存在
            if (tradePayPO == null) {
                CastException.cast(ShopCode.SHOP_PAYMENT_NOT_FOUND);
            }
            tradePayPO.setIsPaid(ShopCode.SHOP_ORDER_PAY_STATUS_IS_PAY.getCode());
            int r = tradePayRepository.update(tradePayPO, queryWrapper);
            log.info("支付订单状态改为已支付");
            if (r == 1) {
                // 3. 创建支付成功的消息
                TradeMQMessagePO tradeMQMessage = new TradeMQMessagePO();
                tradeMQMessage.setId(String.valueOf(idWorker.nextId()));
                tradeMQMessage.setGroupName(groupName);
                tradeMQMessage.setMsgTopic(topic);
                tradeMQMessage.setMsgTag(tag);
                tradeMQMessage.setMsgKey(String.valueOf(tradePay.getPayId()));
                tradeMQMessage.setMsgBody(JSON.toJSONString(tradePay));
                tradeMQMessage.setCreateTime(new Date());
                // 4. 将消息持久化数据库
                tradeMQMessageRepository.insert(tradeMQMessage);
                log.info("将支付成功消息持久化到数据库");

                // 在线程池中进行处理
                threadPoolTaskExecutor.submit(() -> {
                    // 5. 发送消息到MQ
                    SendResult result = null;
                    try {
                        result = sendMessage(topic, tag, String.valueOf(tradePay.getPayId()),
                                JSON.toJSONString(tradePay));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (result.getSendStatus().equals(SendStatus.SEND_OK)) {
                        log.info("消息发送成功");
                        QueryWrapper<TradeMQMessagePO> deleteWrapper = new QueryWrapper<>();
                        deleteWrapper.lambda().eq(TradeMQMessagePO::getId, tradeMQMessage.getId());
                        // 6. 等待发送结果,如果MQ接受到消息,删除发送成功的消息
                        tradeMQMessageRepository.delete(deleteWrapper);
                        log.info("持久化到数据库的消息删除");
                    }
                });

            }
            return new Result(ShopCode.SHOP_SUCCESS.getSuccess(), ShopCode.SHOP_SUCCESS.getMessage());
        } else {
            CastException.cast(ShopCode.SHOP_PAYMENT_PAY_ERROR);
            return new Result(ShopCode.SHOP_FAIL.getSuccess(), ShopCode.SHOP_FAIL.getMessage());
        }
    }

    /**
     * 发送支付成功消息
     *
     * @param topic
     * @param tag
     * @param key
     * @param body
     */
    private SendResult sendMessage(String topic, String tag, String key, String body)
            throws InterruptedException, RemotingException, MQClientException, MQBrokerException {
        if (StringUtils.isEmpty(topic)) {
            CastException.cast(ShopCode.SHOP_MQ_TOPIC_IS_EMPTY);
        }
        if (StringUtils.isEmpty(body)) {
            CastException.cast(ShopCode.SHOP_MQ_MESSAGE_BODY_IS_EMPTY);
        }
        Message message = new Message(topic, tag, key, body.getBytes());
        return rocketMQTemplate.getProducer().send(message);
    }

}
