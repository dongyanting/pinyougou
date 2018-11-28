package com.pyg.manager.controller;

import com.alibaba.fastjson.JSON;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/index")
public class IndexController {

    @RequestMapping("/showName")
    public String showName(){
//        用户名
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return username;
    }

    @RequestMapping("/showNameMap")
    public Map showNameMap(){
//        用户名
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Map map = new HashMap();
        map.put("username",username);
        return map;
    }

    public static void main(String[] args) {
        String username="admin";
        System.out.println(username);
        String jsonString = JSON.toJSONString(username);
        System.out.println(jsonString);

    }




}
