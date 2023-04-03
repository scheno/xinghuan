package com.schening.xinghuan.shop.order.service.impl;

import cn.hutool.json.JSONUtil;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.schening.xinghuan.shop.common.constant.ShopCode;
import com.schening.xinghuan.shop.common.entity.MQEntity;
import com.schening.xinghuan.shop.common.entity.Result;
import com.schening.xinghuan.shop.common.exception.CastException;
import com.schening.xinghuan.shop.coupon.facade.CouponReadFacade;
import com.schening.xinghuan.shop.coupon.facade.CouponWriteFacade;
import com.schening.xinghuan.shop.coupon.model.TradeCoupon;
import com.schening.xinghuan.shop.goods.facade.GoodsReadFacade;
import com.schening.xinghuan.shop.goods.facade.GoodsWriteFacade;
import com.schening.xinghuan.shop.goods.facade.PayWriteFacade;
import com.schening.xinghuan.shop.goods.model.TradeGoods;
import com.schening.xinghuan.shop.goods.model.TradeGoodsNumberLog;
import com.schening.xinghuan.shop.order.converter.TradeOrderConverter;
import com.schening.xinghuan.shop.order.model.TradeOrder;
import com.schening.xinghuan.shop.order.po.TradeOrderPO;
import com.schening.xinghuan.shop.order.repository.TradeOrderRepository;
import com.schening.xinghuan.shop.order.service.OrderService;
import com.schening.xinghuan.shop.user.facade.UserReadFacade;
import com.schening.xinghuan.shop.user.facade.UserWriteFacade;
import com.schening.xinghuan.shop.user.model.TradeUser;
import com.schening.xinghuan.shop.user.model.TradeUserMoneyLog;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

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

    private final CouponReadFacade couponReadFacade;

    private final UserReadFacade userReadFacade;

    private final UserWriteFacade userWriteFacade;

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
     * 确认订单
     *
     * @param order
     */
    private void updateOrderStatus(TradeOrder order) {
        order.setOrderStatus(ShopCode.SHOP_ORDER_CONFIRM.getCode());
        order.setPayStatus(ShopCode.SHOP_ORDER_PAY_STATUS_NO_PAY.getCode());
        order.setConfirmTime(new Date());
        QueryWrapper<TradeOrderPO> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(TradeOrderPO::getOrderId, order.getOrderId());
        TradeOrderPO tradeOrderPO = TradeOrderConverter.INSTANCE.convert(order);
        int r = tradeOrderRepository.update(tradeOrderPO, queryWrapper);
        if (r <= 0) {
            CastException.cast(ShopCode.SHOP_ORDER_CONFIRM_FAIL);
        }
        log.info("订单:" + order.getOrderId() + "确认订单成功");
    }

    /**
     * 账户余额扣减
     *
     * @param order 订单实体
     */
    private void reduceMoneyPaid(TradeOrder order) {
        if (order.getMoneyPaid() != null && order.getMoneyPaid().compareTo(BigDecimal.ZERO) == 1) {
            TradeUserMoneyLog userMoneyLog = new TradeUserMoneyLog();
            userMoneyLog.setOrderId(order.getOrderId());
            userMoneyLog.setUserId(order.getUserId());
            userMoneyLog.setUseMoney(order.getMoneyPaid());
            userMoneyLog.setMoneyLogType(ShopCode.SHOP_USER_MONEY_PAID.getCode());
            Result result = userWriteFacade.updateMoneyPaid(userMoneyLog);
            if (result.getSuccess().equals(ShopCode.SHOP_FAIL.getSuccess())) {
                CastException.cast(ShopCode.SHOP_USER_MONEY_REDUCE_FAIL);
            }
            log.info("订单:" + order.getOrderId() + ",扣减余额成功");
        }
    }

    /**
     * 更新消费券状态
     *
     * @param order 订单实体
     */
    private void updateCouponStatus(TradeOrder order) {
        if (order.getCouponId() != null) {
            TradeCoupon coupon = couponReadFacade.findCouponByCouponId(order.getCouponId());
            coupon.setOrderId(order.getOrderId());
            coupon.setIsUsed(ShopCode.SHOP_COUPON_ISUSED.getCode());
            coupon.setUsedTime(new Date());

            //更新优惠券状态
            Result result = couponWriteFacade.updateCouponStatus(coupon);
            if (result.getSuccess().equals(ShopCode.SHOP_FAIL.getSuccess())) {
                CastException.cast(ShopCode.SHOP_COUPON_USE_FAIL);
            }
            log.info("订单:" + order.getOrderId() + ",使用优惠券");
        }
    }

    /**
     * 扣减库存
     *
     * @param order 订单实体
     */
    private void reduceGoodsNum(TradeOrder order) {
        TradeGoodsNumberLog goodsNumberLog = new TradeGoodsNumberLog();
        goodsNumberLog.setOrderId(order.getOrderId());
        goodsNumberLog.setGoodsId(order.getGoodsId());
        goodsNumberLog.setGoodsNumber(order.getGoodsNumber());
        Result result = goodsWriteFacade.reduceGoodsNum(goodsNumberLog);
        if (result.getSuccess().equals(ShopCode.SHOP_FAIL.getSuccess())) {
            CastException.cast(ShopCode.SHOP_REDUCE_GOODS_NUM_FAIL);
        }
        log.info("订单:" + order.getOrderId() + "扣减库存成功");
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
        // 2.校验订单中的商品是否存在
        TradeGoods goods = goodsReadFacade.findGoodsByGoodsId(order.getGoodsId());
        if (goods == null) {
            CastException.cast(ShopCode.SHOP_GOODS_NO_EXIST);
        }
        // 3.校验下单用户是否存在
        TradeUser user = userReadFacade.findUserByUserId(order.getUserId());
        if (user == null) {
            CastException.cast(ShopCode.SHOP_USER_NO_EXIST);
        }
        // 4.校验商品单价是否合法
        if (order.getGoodsPrice().compareTo(goods.getGoodsPrice()) != 0) {
            CastException.cast(ShopCode.SHOP_GOODS_PRICE_INVALID);
        }
        // 5.校验订单商品数量是否合法
        if (order.getGoodsNumber() >= goods.getGoodsNumber()) {
            CastException.cast(ShopCode.SHOP_GOODS_NUM_NOT_ENOUGH);
        }

        log.info("校验订单通过");
    }

    /**
     * 发送订单确认失败消息
     *
     * @param topic 主题
     * @param tag   标签
     * @param keys
     * @param body  消息体
     */
    private void sendCancelOrder(String topic, String tag, String keys, String body)
            throws MQBrokerException, RemotingException, InterruptedException, MQClientException {
        Message message = new Message(topic, tag, keys, body.getBytes());
        rocketMQTemplate.getProducer().send(message);
    }

}
