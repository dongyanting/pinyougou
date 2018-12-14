package com.pyg.seckill.service;

import com.pyg.pojo.TbSeckillGoods;

import java.util.List;

public interface SeckillService {
    List<TbSeckillGoods> findAllSeckillGoodsFromRedis();

    TbSeckillGoods findOneSeckillGoodsFromRedis(Long id);

    void saveOrder(Long id,String userId);
}
