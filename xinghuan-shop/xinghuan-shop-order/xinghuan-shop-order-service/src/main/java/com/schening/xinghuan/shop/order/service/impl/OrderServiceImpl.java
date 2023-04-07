package com.schening.xinghuan.shop.order.service.impl;

import cn.hutool.json.JSONUtil;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.schening.xinghuan.shop.common.constant.ShopCode;
import com.schening.xinghuan.shop.common.entity.MQEntity;
import com.schening.xinghuan.shop.common.entity.Result;
import com.schening.xinghuan.shop.common.exception.CastException;
import com.schening.xinghuan.shop.common.utils.IDWorker;
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
import org.springframework.transaction.annotation.Transactional;

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

    private final IDWorker idWorker;

    private final TradeOrderRepository tradeOrderRepository;

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
            // 模拟异常抛出
            CastException.cast(ShopCode.SHOP_FAIL);
            // 3.扣减库存
            reduceGoodsNum(order);
            // 4.扣减优惠券
            updateCouponStatus(order);
            // 5.使用余额
            reduceMoneyPaid(order);

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

            // 更新优惠券状态
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
    private long savePreOrder(TradeOrder order) {
        // 1. 设置订单状态为不可见
        order.setOrderStatus(ShopCode.SHOP_ORDER_NO_CONFIRM.getCode());
        // 2. 设置订单ID
        long orderId = idWorker.nextId();
        order.setOrderId(orderId);
        // 3. 核算订单运费
//        BigDecimal shippingFee = calculateShippingFee(order.getOrderAmount());
//        if(order.getShippingFee().compareTo(shippingFee)!=0){
//            CastException.cast(ShopCode.SHOP_ORDER_SHIPPINGFEE_INVALID);
//        }
        // 4. 核算订单总金额是否合法
//        BigDecimal orderAmount = order.getGoodsPrice().multiply(new BigDecimal(order.getGoodsNumber()));
//        orderAmount.add(shippingFee);
//        if(order.getOrderAmount().compareTo(orderAmount)!=0){
//            CastException.cast(ShopCode.SHOP_ORDERAMOUNT_INVALID);
//        }
        // 5.判断用户是否使用余额
//        BigDecimal moneyPaid = order.getMoneyPaid();
//        if(moneyPaid!=null){
            // 5.1 订单中余额是否合法
//            int r = moneyPaid.compareTo(BigDecimal.ZERO);

            // 余额小于0
//            if(r==-1){
//                CastException.cast(ShopCode.SHOP_MONEY_PAID_LESS_ZERO);
//            }

            // 余额大于0
//            if(r==1){
//                TradeUser user = userService.findOne(order.getUserId());

//                if(moneyPaid.compareTo(new BigDecimal(user.getUserMoney()))==1){
//                    CastException.cast(ShopCode.SHOP_MONEY_PAID_INVALID);
//                }
//            }

//        }else{
//            order.setMoneyPaid(BigDecimal.ZERO);
//        }
        //6.判断用户是否使用优惠券
//        Long couponId = order.getCouponId();
//        if(couponId!=null){
//            TradeCoupon coupon = couponService.findOne(couponId);
            //6.1 判断优惠券是否存在
//            if(coupon==null){
//                CastException.cast(ShopCode.SHOP_COUPON_NO_EXIST);
//            }
            //6.2 判断优惠券是否已经被使用
//            if(coupon.getIsUsed().intValue()==ShopCode.SHOP_COUPON_ISUSED.getCode().intValue()){
//                CastException.cast(ShopCode.SHOP_COUPON_ISUSED);
//            }

//            order.setCouponPaid(coupon.getCouponPrice());

//        }else{
//            order.setCouponPaid(BigDecimal.ZERO);
//        }
        //7.核算订单支付金额    订单总金额-余额-优惠券金额
//        BigDecimal payAmount = order.getOrderAmount().subtract(order.getMoneyPaid()).subtract(order.getCouponPaid());
//        order.setPayAmount(payAmount);
        //8.设置下单时间
        order.setAddTime(new Date());
        //9.保存订单到数据库
        TradeOrderPO tradeOrder = TradeOrderConverter.INSTANCE.convert(order);
        tradeOrderRepository.insert(tradeOrder);
        //10.返回订单ID
        return orderId;
    }

    /**
     * 校验订单
     *
     * @param order 订单实体
     */
    private void checkOrder(TradeOrder order) {
        // 1.校验订单是否存在
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
        rocketMQTemplate.getProducer().send(message, 15000);
    }

}
