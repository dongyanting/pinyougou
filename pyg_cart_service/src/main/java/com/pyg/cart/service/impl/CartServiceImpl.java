package com.pyg.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pyg.cart.service.CartService;
import com.pyg.mapper.TbItemMapper;
import com.pyg.pojo.TbItem;
import com.pyg.pojo.TbOrderItem;
import groupEntity.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private TbItemMapper itemMapper;


    @Override
    public List<Cart> findCartListFromRedis(String userKey) {

//        List<Cart> cartList = redisTemplate.boundValueOps("cartList").get();
        // 相当于开辟了一块空间名字叫cartList，通过传过来的userkey
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(userKey);

        // 如果购物车为空，返回一个空对象
        if (cartList == null) {
            cartList = new ArrayList<>();
        }

        return cartList;
    }


    @Override
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, int num) {
        TbItem tbItem = itemMapper.selectByPrimaryKey(itemId);
        if(tbItem==null){
            throw new RuntimeException("没有此商品!");
        }
        String sellerId = tbItem.getSellerId();
        // 1、根据sku中的sellerId从cartList中查询此商家对应的cart对象
        Cart cart = findCartFromCartListBySellerId(cartList,sellerId);
//        1.1 如果能查询出cart对象（cart!=null）
        if(cart!=null){
            // 1.1.1 还需要判断此sku是否在此cart对象的orderItemList中
            List<TbOrderItem> orderItemList = cart.getOrderItemList();
            TbOrderItem orderItem = findOrderItemFromOrderItemListByItemId(orderItemList,itemId);
//        1.1.1.1如果存在当前的orderItem数量累加 小计金额重新计算
            if(orderItem!=null){
                orderItem.setNum(orderItem.getNum()+num); //数量变化  需要考虑减数量的情况
                double totalFee = orderItem.getPrice().doubleValue() * orderItem.getNum(); //小计金额
                orderItem.setTotalFee(new BigDecimal(totalFee));
                if(orderItem.getNum()<=0){  //考虑减数量的情况
//                    把orderItem从orderItemList中移除
                    orderItemList.remove(orderItem);
//                    判断此购物车对象中是否有商品
                    if(orderItemList.size()==0){  //如果一个商家中没有商品了 应该移除此购物车对象
                        cartList.remove(cart);
                    }
                }
            }else{
                //  1.1.1.2如果不存在当前的orderItem
//                创建一个新的orderItem对象,追加到orderItemList中
                orderItem= new TbOrderItem();
                orderItem = createOrderItem(orderItem, num, tbItem);
                orderItemList.add(orderItem);
            }

        }else{
            //        1.2 没有查询出cart对象 （cart==null）
//        创建一个新的cart对象
            cart = new Cart();
            cart.setSellerId(tbItem.getSellerId());
            cart.setSellerName(tbItem.getSeller());
            List<TbOrderItem> orderItemList = new ArrayList<TbOrderItem>();
//                创建一个新的orderItemList
//        创建一个新的orderItem对象,追加到orderItemList中
            TbOrderItem orderItem = new TbOrderItem();
            orderItem = createOrderItem(orderItem, num, tbItem);
            orderItemList.add(orderItem);
            cart.setOrderItemList(orderItemList);
//                把新创建的cart放入到cartList中
            cartList.add(cart);
        }
        return cartList;
    }


    private TbOrderItem createOrderItem(TbOrderItem orderItem,int num,TbItem tbItem) {
        orderItem.setNum(num);
        orderItem.setPrice(tbItem.getPrice());
        double totalFee = orderItem.getNum() * orderItem.getPrice().doubleValue();  // 计算的方法：数量乘以价钱
        orderItem.setTotalFee(new BigDecimal(totalFee));
        orderItem.setItemId(tbItem.getId());
        orderItem.setGoodsId(tbItem.getGoodsId());
//            orderItem.setId();   // TODO插入到MySQL时才用
//            orderItem.setOrderId(); // TODO插入到MySQL时才用
        orderItem.setPicPath(tbItem.getImage());
        orderItem.setTitle(tbItem.getTitle());
        orderItem.setSellerId(tbItem.getSellerId());

        return orderItem;
    }

    // 根据itemId从orderItemList中查找orderItem对象
    private TbOrderItem findOrderItemFromOrderItemListByItemId(List<TbOrderItem> orderItemList, Long itemId) {
        for (TbOrderItem orderItem : orderItemList) {
            if (orderItem.getItemId().longValue() == itemId.longValue()) {
                return orderItem;
            }
        }
        return null;
    }

    // 根据sellerId从cartList中查找cart对象
    private Cart findCartFromCartListBySellerId(List<Cart> cartList, String sellerId) {
        for (Cart cart : cartList) {
            if (cart.getSellerId().equals(sellerId)) {
                return cart;
            }
        }
        return null;
    }

    @Override
    public void saveCarListToRedis(String userKey, List<Cart> cartList) {
        redisTemplate.boundHashOps("cartList").put(userKey, cartList);
    }

    @Override
    public List<Cart> mergeCartList(List<Cart> cartList_sessionId, List<Cart> cartList_userId) {
//        合并就是把cartList_sessionId中的每一个商品合并到cartList_userId中
        for (Cart cart : cartList_sessionId) {
            List<TbOrderItem> orderItemList = cart.getOrderItemList();
            for (TbOrderItem orderItem : orderItemList) {
                cartList_userId = addGoodsToCartList(cartList_userId, orderItem.getItemId(), orderItem.getNum());
            }
        }
        return cartList_userId;

    }

    @Override
    public void deleteCartListByKey(String sessionId) {
        redisTemplate.boundHashOps("cartList").delete(sessionId);
    }
}
