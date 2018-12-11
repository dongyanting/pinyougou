package com.pyg.cart.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.pyg.cart.service.CartService;
import entity.Result;
import groupEntity.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import utils.CookieUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private HttpSession httpSession;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;


    // 通过UUID来获取sessionId,并且保存在cookie中，注意：保存的并不是商品的具体信息，因为cookie的
    // 值最大是4kb，所以可以通过保存商品生成的UUID来保存字符串存储到cookie 中
    private String getSessionId() {
        String userkey = CookieUtil.getCookieValue(request, "user-key");
        if (userkey == null) {
            userkey = UUID.randomUUID().toString();
            CookieUtil.setCookie(request,response,"user-key",userkey);
        }
        return userkey;
    }


    @Reference
    private CartService cartService;

    @RequestMapping("/findCartList")
    public List<Cart> findCartList() {
        String sessionId = getSessionId();
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        List<Cart> cartList_sessionId = cartService.findCartListFromRedis(getSessionId()); // 没有登录时的购物车列表

        if (userId.equals("anonymousUser")) { // 此时表示未登录
            return cartList_sessionId;
        } else { // 表示已经登录
            List<Cart> cartList_userId = cartService.findCartListFromRedis(userId);
//            已经登录，判断cartList_sessionId中是否有数据
            if (cartList_sessionId.size()>0) { // 有数据，进行合并
                cartList_userId = cartService.mergeCartList(cartList_sessionId,cartList_userId);
                // 合并以后从redis中清除cartList_sessionId
                cartService.deleteCartListByKey(sessionId);
                // 并且把合并后的数据放入到redis中
                cartService.saveCarListToRedis(userId,cartList_userId);
            }
            return cartList_userId;
        }
    }


//    因为添加的结果有成功或者失败的可能所以用Result来接收,传过来的参数只需要商品的id和数量就就可以
    @RequestMapping("/addGoodsToCartList")
//    @CrossOrigin(origins = {"http://item.pinyougou.com","http://www.pinyougou.com"}) // 允许http://item.pinyougou.com对此方法进行跨域访问
    @CrossOrigin(origins = "*") //允许所有网站访问
    public Result addGoodsToCartList(Long itemId,int num) {

        try {
            // 首先需要查询一下购物车列表 也就是原有的购物车列表
            List<Cart> cartList = findCartList();
            // 添加购物车
            cartList = cartService.addGoodsToCartList(cartList,itemId,num);

            // 然后把购物车的数据存放到Redis中 需要传入key就是getSessionId和value就是cartList
            String userKey = getSessionId();
            String userId = SecurityContextHolder.getContext().getAuthentication().getName();
            if (userId.equals("anonymousUser")) { // 未登录
                cartService.saveCarListToRedis(userKey,cartList);
            } else {
                cartService.saveCarListToRedis(userId,cartList);
            }

            return new Result(true,"");
        } catch (RuntimeException e) {
            return new Result(false,e.getMessage());
        }catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"商品添加失败");
        }

    }
}
