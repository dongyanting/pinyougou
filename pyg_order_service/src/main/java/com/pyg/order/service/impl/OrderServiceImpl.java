package com.pyg.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pyg.mapper.TbOrderItemMapper;
import com.pyg.mapper.TbOrderMapper;
import com.pyg.mapper.TbPayLogMapper;
import com.pyg.order.service.OrderService;
import com.pyg.pojo.TbOrder;
import com.pyg.pojo.TbOrderItem;
import com.pyg.pojo.TbPayLog;
import groupEntity.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import utils.IdWorker;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    @Autowired
    private IdWorker idWorker;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private TbOrderMapper orderMapper;
    @Autowired
    private TbOrderItemMapper orderItemMapper;

    @Autowired
    private TbPayLogMapper payLogMapper;
    @Override
    public void add(TbOrder order) {  //order
        String userId = order.getUserId();
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(userId);

        String orderList="";
        Double totalFee = 0.00;

        for (Cart cart : cartList) {
            TbOrder tbOrder = new TbOrder();
//  `payment_type` varchar(1) COLLATE utf8_bin DEFAULT NULL COMMENT '支付类型，1、在线支付 微信，2、货到付款',
//  `receiver_area_name` varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '收货人地区名称(省，市，县)街道',
//  `receiver_mobile` varchar(12) COLLATE utf8_bin DEFAULT NULL COMMENT '收货人手机',
//  `receiver` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT '收货人',
//  `source_type` varchar(1) COLLATE utf8_bin DEFAULT NULL COMMENT '订单来源：1:app端，2：pc端，3：M端，4：微信端，5：手机qq端',
            tbOrder.setPaymentType(order.getPaymentType());
            tbOrder.setReceiver(order.getReceiver());
            tbOrder.setReceiverMobile(order.getReceiverMobile());
            tbOrder.setReceiverAreaName(order.getReceiverAreaName());
            tbOrder.setSourceType(order.getSourceType());
//                后台需要默认赋的值有：
            long orderId = idWorker.nextId();
            //  `order_id` bigint(20) NOT NULL COMMENT '订单id',
            tbOrder.setOrderId(orderId);
//  `seller_id` varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '商家ID',
            tbOrder.setSellerId(cart.getSellerId());
//  `status` varchar(1) COLLATE utf8_bin DEFAULT NULL COMMENT '状态：1、未付款，2、已付款，3、未发货，4、已发货，5、交易成功，6、交易关闭,7、待评价',
            tbOrder.setStatus("1");
//  `create_time` datetime DEFAULT NULL COMMENT '订单创建时间',
//  `update_time` datetime DEFAULT NULL COMMENT '订单更新时间',
            tbOrder.setCreateTime(new Date());
            tbOrder.setUpdateTime(new Date());
            //  `user_id` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT '用户id',
            tbOrder.setUserId(userId);
//  `payment` decimal(20,2) DEFAULT NULL COMMENT '实付金额。精确到2位小数;单位:元。如:200.07，表示:200元7分',
            List<TbOrderItem> orderItemList = cart.getOrderItemList();
            BigDecimal payment = new BigDecimal("0");
            for (TbOrderItem orderItem : orderItemList) {
                payment=payment.add(orderItem.getTotalFee());
                //                订单项：tb_order_item
                orderItem.setId(idWorker.nextId());
                orderItem.setOrderId(orderId);
                orderItemMapper.insert(orderItem);
            }
            totalFee += payment.doubleValue();
            tbOrder.setPayment(payment);
            orderMapper.insert(tbOrder);
            orderList+=orderId+",";
        }
//        清空购物车
        redisTemplate.boundHashOps("cartList").delete(userId);

//        插入一个支付日志数据
        TbPayLog payLog = new TbPayLog();
        payLog.setCreateTime(new Date());   // 创建时间
        payLog.setOrderList(orderList.substring(0,orderList.length()-1));    // 订单id
        payLog.setOutTradeNo(idWorker.nextId()+"");   // 主键
//        payLog.setPayTime();      // 支付时间   TODO 支付以后
        payLog.setPayType(order.getPaymentType());      // 支付方式
        payLog.setTotalFee((long)(totalFee*100));     // 总金额
        payLog.setTradeState("0");   // 支付状态
//        payLog.setTransactionId();// 微信交易流水号 ，TODO支付成功后才有值
        payLog.setUserId(userId);       // 用户id

        payLogMapper.insert(payLog);

        // 为了方便支付获取paylog对象，放入redis
        redisTemplate.boundHashOps("payLog").put(userId,payLog);
    }
}
