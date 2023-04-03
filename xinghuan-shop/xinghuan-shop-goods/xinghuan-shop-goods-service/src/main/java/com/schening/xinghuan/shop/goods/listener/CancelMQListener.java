package com.schening.xinghuan.shop.goods.listener;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.schening.xinghuan.shop.common.constant.ShopCode;
import com.schening.xinghuan.shop.common.entity.MQEntity;
import com.schening.xinghuan.shop.goods.model.TradeGoods;
import com.schening.xinghuan.shop.goods.po.TradeGoodsPO;
import com.schening.xinghuan.shop.goods.po.TradeMQConsumerLogPO;
import com.schening.xinghuan.shop.goods.po.TradeMQConsumerLogKey;
import com.schening.xinghuan.shop.goods.repository.TradeGoodsNumberLogRepository;
import com.schening.xinghuan.shop.goods.repository.TradeGoodsRepository;
import com.schening.xinghuan.shop.goods.repository.TradeMQConsumerLogRepository;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author shenchen
 * @version 1.0
 * @date 2023/4/3 17:49
 */
@Slf4j
@Component
@RocketMQMessageListener(topic = "${mq.order.topic}", consumerGroup = "${mq.order.consumer.group.name}", messageModel = MessageModel.BROADCASTING)
public class CancelMQListener implements RocketMQListener<MessageExt> {


    @Value("${mq.order.consumer.group.name}")
    private String groupName;

    @Autowired
    private TradeGoodsRepository tradeGoodsRepository;

    @Autowired
    private TradeMQConsumerLogRepository tradeMQConsumerLogRepository;

    @Autowired
    private TradeGoodsNumberLogRepository tradeGoodsNumberLogRepository;

    @Override
    public void onMessage(MessageExt messageExt) {
        String msgId = null;
        String tags = null;
        String keys = null;
        String body = null;
        try {
            // 1. 解析消息内容
            msgId = messageExt.getMsgId();
            tags = messageExt.getTags();
            keys = messageExt.getKeys();
            body = new String(messageExt.getBody(), "UTF-8");

            log.info("接受消息成功");

            // 2. 查询消息消费记录
            QueryWrapper<TradeMQConsumerLogPO> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(TradeMQConsumerLogPO::getMsgTag, tags).eq(TradeMQConsumerLogPO::getMsgId, msgId).eq(TradeMQConsumerLogKey::getMsgKey, keys);
            TradeMQConsumerLogPO mqConsumerLog = tradeMQConsumerLogRepository.selectOne(queryWrapper);

            if (mqConsumerLog != null) {
                // 3. 判断如果消费过...
                // 3.1 获得消息处理状态
                Integer status = mqConsumerLog.getConsumerStatus();
                // 处理过...返回
                if (ShopCode.SHOP_MQ_MESSAGE_STATUS_SUCCESS.getCode().intValue()
                        == status.intValue()) {
                    log.info("消息:" + msgId + ",已经处理过");
                    return;
                }

                // 正在处理...返回
                if (ShopCode.SHOP_MQ_MESSAGE_STATUS_PROCESSING.getCode().intValue()
                        == status.intValue()) {
                    log.info("消息:" + msgId + ",正在处理");
                    return;
                }

                // 处理失败
                if (ShopCode.SHOP_MQ_MESSAGE_STATUS_FAIL.getCode().intValue()
                        == status.intValue()) {
                    // 获得消息处理次数
                    Integer times = mqConsumerLog.getConsumerTimes();
                    if (times > 3) {
                        log.info("消息:" + msgId + ",消息处理超过3次,不能再进行处理了");
                        return;
                    }
                    mqConsumerLog.setConsumerStatus(
                            ShopCode.SHOP_MQ_MESSAGE_STATUS_PROCESSING.getCode());

                    // 使用数据库乐观锁更新
                    queryWrapper.lambda().eq(TradeMQConsumerLogPO::getConsumerTimes, mqConsumerLog.getConsumerTimes());
                    int r = tradeMQConsumerLogRepository.update(mqConsumerLog, queryWrapper);
                    if (r <= 0) {
                        // 未修改成功,其他线程并发修改
                        log.info("并发修改,稍后处理");
                    }
                }

            } else {
                // 4. 判断如果没有消费过...
                mqConsumerLog = new TradeMQConsumerLogPO();
                mqConsumerLog.setMsgTag(tags);
                mqConsumerLog.setMsgKey(keys);
                mqConsumerLog.setGroupName(groupName);
                mqConsumerLog.setConsumerStatus(
                        ShopCode.SHOP_MQ_MESSAGE_STATUS_PROCESSING.getCode());
                mqConsumerLog.setMsgBody(body);
                mqConsumerLog.setMsgId(msgId);
                mqConsumerLog.setConsumerTimes(0);

                // 将消息处理信息添加到数据库
                tradeMQConsumerLogRepository.insert(mqConsumerLog);
            }
            // 5. 回退库存
            MQEntity mqEntity = JSON.parseObject(body, MQEntity.class);
            Long goodsId = mqEntity.getGoodsId();
            TradeGoodsPO goods = tradeGoodsRepository.selectOne(new QueryWrapper<TradeGoodsPO>().lambda().eq(TradeGoodsPO::getGoodsId, goodsId));
            goods.setGoodsNumber(goods.getGoodsNumber() + mqEntity.getGoodsNum());
            tradeGoodsRepository.update(goods, new QueryWrapper<TradeGoodsPO>().lambda().eq(TradeGoodsPO::getGoodsId, goodsId));

            //6. 将消息的处理状态改为成功
            mqConsumerLog.setConsumerStatus(ShopCode.SHOP_MQ_MESSAGE_STATUS_SUCCESS.getCode());
            mqConsumerLog.setConsumerTimestamp(new Date());
            queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(TradeMQConsumerLogPO::getMsgTag, tags).eq(TradeMQConsumerLogPO::getMsgId, msgId).eq(TradeMQConsumerLogKey::getMsgKey, keys);
            tradeMQConsumerLogRepository.update(mqConsumerLog, queryWrapper);
            log.info("回退库存成功");
        } catch (Exception e) {
            e.printStackTrace();
            QueryWrapper<TradeMQConsumerLogPO> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(TradeMQConsumerLogPO::getMsgTag, tags).eq(TradeMQConsumerLogPO::getMsgId, msgId).eq(TradeMQConsumerLogKey::getMsgKey, keys);
            TradeMQConsumerLogPO mqConsumerLog = tradeMQConsumerLogRepository.selectOne(queryWrapper);
            if (mqConsumerLog == null) {
                //数据库未有记录
                mqConsumerLog = new TradeMQConsumerLogPO();
                mqConsumerLog.setMsgTag(tags);
                mqConsumerLog.setMsgKey(keys);
                mqConsumerLog.setGroupName(groupName);
                mqConsumerLog.setConsumerStatus(ShopCode.SHOP_MQ_MESSAGE_STATUS_FAIL.getCode());
                mqConsumerLog.setMsgBody(body);
                mqConsumerLog.setMsgId(msgId);
                mqConsumerLog.setConsumerTimes(1);
                tradeMQConsumerLogRepository.insert(mqConsumerLog);
            } else {
                mqConsumerLog.setConsumerTimes(mqConsumerLog.getConsumerTimes() + 1);
                tradeMQConsumerLogRepository.update(mqConsumerLog, queryWrapper);
            }
        }

    }
}
