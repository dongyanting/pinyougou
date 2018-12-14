package com.pyg.seckill.service.impl;

import com.pyg.mapper.TbSeckillGoodsMapper;
import com.pyg.mapper.TbSeckillOrderMapper;
import com.pyg.pojo.TbSeckillGoods;
import com.pyg.pojo.TbSeckillOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import utils.IdWorker;

import java.util.Date;
import java.util.Map;

@Component
public class CreateOrder implements Runnable{

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private TbSeckillOrderMapper seckillOrderMapper;
    @Autowired
    private TbSeckillGoodsMapper seckillGoodsMapper;
    @Override
    public void run() {
        Map map = (Map) redisTemplate.boundListOps("userid_id").leftPop();
        Long id = (Long) map.get("id");
        String userId = (String) map.get("userId");

        TbSeckillGoods seckillGoods = (TbSeckillGoods) redisTemplate.boundHashOps("seckill_goods").get(id);

        TbSeckillOrder seckillOrder = new TbSeckillOrder();
        seckillOrder.setId(idWorker.nextId());
        seckillOrder.setSeckillId(id);
        seckillOrder.setMoney(seckillGoods.getCostPrice());
        seckillOrder.setUserId(userId);
        seckillOrder.setSellerId(seckillGoods.getSellerId());
        seckillOrder.setCreateTime(new Date());
        seckillOrder.setStatus("0");
        seckillOrderMapper.insert(seckillOrder);
//        减库存 规则：如果减完库存后为0 同步更新到mysql中 并且从redis中中移除
        seckillGoods.setStockCount(seckillGoods.getStockCount()-1);
        if(seckillGoods.getStockCount()==0){
            seckillGoodsMapper.updateByPrimaryKey(seckillGoods);
            redisTemplate.boundHashOps("seckill_goods").delete(id);
        }else{
            redisTemplate.boundHashOps("seckill_goods").put(id,seckillGoods);
        }

        redisTemplate.boundHashOps("seckill_order").put(userId,seckillOrder); //把个人下的单放入到redis中

    }
}
