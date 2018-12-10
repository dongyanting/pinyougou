package com.pyg.cart.service;

import groupEntity.Cart;

import java.util.List;

public interface CartService {
    List<Cart> findCartListFromRedis(String sessionId);

    List<Cart> addGoodsToCartList(List<Cart> cartList,Long itemId, int num);

    void saveCarListToRedis(String userKey, List<Cart> cartList);

    List<Cart> mergeCartList(List<Cart> cartList_sessionId, List<Cart> cartList_userId);

    void deleteCartListByKey(String sessionId);
}
