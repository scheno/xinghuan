package com.schening.xinghuan.shop.order.service.impl;

import cn.hutool.json.JSONUtil;
import com.schening.xinghuan.shop.common.constant.ShopCode;
import com.schening.xinghuan.shop.common.entity.MQEntity;
import com.schening.xinghuan.shop.common.entity.Result;
import com.schening.xinghuan.shop.common.exception.CastException;
import com.schening.xinghuan.shop.coupon.facade.CouponWriteFacade;
import com.schening.xinghuan.shop.goods.facade.GoodsReadFacade;
import com.schening.xinghuan.shop.goods.facade.GoodsWriteFacade;
import com.schening.xinghuan.shop.goods.facade.PayWriteFacade;
import com.schening.xinghuan.shop.order.model.TradeOrder;
import com.schening.xinghuan.shop.order.po.TradeOrderPO;
import com.schening.xinghuan.shop.order.repository.TradeOrderRepository;
import com.schening.xinghuan.shop.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author shenchen
 * @version 1.0
 * @date 2023/3/22 16:02
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    @Value("${mq.order.topic}")
    private String topic;

    @Value("${mq.order.tag.cancel}")
    private String tag;

    private final TradeOrderRepository tradeOrderRepository;

    private final PayWriteFacade payWriteFacade;

    private final GoodsReadFacade goodsReadFacade;

    private final GoodsWriteFacade goodsWriteFacade;

    private final CouponWriteFacade couponWriteFacade;

    private final RocketMQTemplate rocketMQTemplate;

    @Override
    public Result confirmOrder(TradeOrder order) {
        // 1.校验订单
        checkOrder(order);
        // 2.生成预订单
        long orderId = savePreOrder(order);
        try {
            // 3.扣减库存
            reduceGoodsNum(order);
            // 4.扣减优惠券
            updateCouponStatus(order);
            // 5.使用余额
            reduceMoneyPaid(order);

            // 模拟异常抛出
            // CastException.cast(ShopCode.SHOP_FAIL);

            // 6.确认订单
            updateOrderStatus(order);
            // 7.返回成功状态
            return new Result(ShopCode.SHOP_SUCCESS.getSuccess(),
                    ShopCode.SHOP_SUCCESS.getMessage());
        } catch (Exception e) {
            // 1.确认订单失败,发送消息
            MQEntity entity = MQEntity.builder()
                    .orderId(orderId)
                    .userId(order.getUserId())
                    .userMoney(order.getMoneyPaid())
                    .goodsId(orderId)
                    .goodsNum(order.getGoodsNumber())
                    .couponId(order.getCouponId())
                    .build();

            // 2.返回订单确认失败消息
            try {
                sendCancelOrder(topic, tag, order.getOrderId().toString(),
                        JSONUtil.toJsonStr(entity));
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            return new Result(ShopCode.SHOP_FAIL.getSuccess(), ShopCode.SHOP_FAIL.getMessage());
        }
    }

    /**
     * 更新订单状态
     *
     * @param order 订单实体
     */
    private void updateOrderStatus(TradeOrder order) {
    }

    /**
     * 账户余额扣减
     *
     * @param order 订单实体
     */
    private void reduceMoneyPaid(TradeOrder order) {
    }

    /**
     * 更新消费券状态
     *
     * @param order 订单实体
     */
    private void updateCouponStatus(TradeOrder order) {
    }

    /**
     * 扣减库存
     *
     * @param order 订单实体
     */
    private void reduceGoodsNum(TradeOrder order) {
    }

    /**
     * 保存订单
     *
     * @param order 订单实体
     * @return 订单编号
     */
    private int savePreOrder(TradeOrder order) {
        TradeOrderPO tradeOrder = new TradeOrderPO();
        return tradeOrderRepository.insert(tradeOrder);
    }

    /**
     * 校验订单
     *
     * @param order 订单实体
     */
    private void checkOrder(TradeOrder order) {
        //1.校验订单是否存在
        if (order == null) {
            CastException.cast(ShopCode.SHOP_ORDER_INVALID);
        }
    }

    /**
     * 发送订单确认失败消息
     *
     * @param topic 主题
     * @param tag 标签
     * @param keys
     * @param body 消息体
     */
    private void sendCancelOrder(String topic, String tag, String keys, String body)
            throws MQBrokerException, RemotingException, InterruptedException, MQClientException {
        Message message = new Message(topic, tag, keys, body.getBytes());
        rocketMQTemplate.getProducer().send(message);
    }

}
