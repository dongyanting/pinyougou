package com.pyg.pay.service;

import entity.Result;

import java.util.Map;

public interface PayService {
    Map createNative(String userId);

    Map queryOrder(String out_trade_no) throws Exception;

    void updateOrder(String name, String transactionId);
}
