package com.pyg.pay.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.pyg.pay.service.PayService;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/pay")
public class PayController {

    @Reference
    private PayService payService;

    @RequestMapping("/createNative")
    public Map createNative() {
        return payService.createNative(SecurityContextHolder.getContext().getAuthentication().getName());
    }
    @RequestMapping("/queryOrder")
    public Result queryOrder(String out_trade_no) {
        try {


            int times = 0;
            while (times<10){
                Map resultMap =  payService.queryOrder(out_trade_no);
                if (resultMap.get("trade_state").equals("SUCCESS")) {

                    String transactionId = (String) resultMap.get("transaction_id");
                    payService.updateOrder(SecurityContextHolder.getContext().getAuthentication().getName(),transactionId);
                    return new Result(true,"");
                }

                Thread.sleep(3000);
                times ++ ;
                System.out.println("times:" + times);
            }
            return new Result(false,"二维码失效");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Result(false,"支付失败");
    }
}
