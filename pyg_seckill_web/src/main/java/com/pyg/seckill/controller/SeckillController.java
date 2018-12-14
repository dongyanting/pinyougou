package com.pyg.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pyg.pojo.TbSeckillGoods;
import com.pyg.seckill.service.SeckillService;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/seckill")
public class SeckillController {

    @Reference
    private SeckillService seckillService;

    @RequestMapping("/findSeckillGoods")
    public List<TbSeckillGoods> findSeckillGoods(){
      return   seckillService.findAllSeckillGoodsFromRedis();
    }
    @RequestMapping("/findOne")
    public TbSeckillGoods findOne(Long id){
      return   seckillService.findOneSeckillGoodsFromRedis(id);
    }
    @RequestMapping("/saveOrder")
    public Result saveOrder(Long id){
        try {
            String userId = SecurityContextHolder.getContext().getAuthentication().getName();
            if(userId.equals("anonymousUser")){
                return new Result(false,"请先登录");
            }

            seckillService.saveOrder(id,userId);
            return new Result(true,"");
        }catch (RuntimeException e){
            return new Result(false,e.getMessage());
        }
        catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"抢购失败");
        }
    }


}
