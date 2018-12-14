package com.pyg.seckill.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.dubbo.registry.redis.RedisRegistry;
import com.pyg.mapper.TbSeckillGoodsMapper;
import com.pyg.mapper.TbSeckillOrderMapper;
import com.pyg.pojo.TbSeckillGoods;
import com.pyg.pojo.TbSeckillOrder;
import com.pyg.seckill.service.SeckillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import utils.IdWorker;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SeckillServiceImpl implements SeckillService {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private ThreadPoolTaskExecutor executor;
    @Autowired
    private CreateOrder createOrder;

    @Override
    public List<TbSeckillGoods> findAllSeckillGoodsFromRedis() {
        return redisTemplate.boundHashOps("seckill_goods").values();
    }

    @Override
    public TbSeckillGoods findOneSeckillGoodsFromRedis(Long id) {
        return (TbSeckillGoods) redisTemplate.boundHashOps("seckill_goods").get(id);
    }

    @Override
    public void saveOrder(Long id, String userId) {
        Object object = redisTemplate.boundHashOps("seckill_order").get(userId);//判断是否下过单
        if(object!=null){
            throw new RuntimeException("请先支付您其他的秒杀订单");
        }

//        采用redis出栈的方式控制超卖问题
        Object o = redisTemplate.boundListOps("seckill_goods_" + id).leftPop();
        if(o==null){
            throw new RuntimeException("商品已售罄");
        }

//    把多线程中需要的参数通过redis队列传递过去
        Map map = new HashMap();
        map.put("id",id);
        map.put("userId",userId);
        redisTemplate.boundListOps("userid_id").rightPush(map);
        //        触发多线程的代码
        executor.execute(createOrder);
    }

}
