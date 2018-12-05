package com.pyg.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pyg.pojo.TbUser;
import com.pyg.user.service.UserService;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @Reference
    private UserService userService;

    @RequestMapping("/sendSms")
    public Result sendSms(String phone) {
        //
        try {
            userService.sendSms(phone);
            return new Result(true,"发送成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"发送失败");
        }
    }

    @RequestMapping("/register")
    public Result register(@RequestBody TbUser user, String code) {
        try {
            userService.register(user,code);
            return new Result(true,"");
        }catch (RuntimeException e) {
            return new Result(false,e.getMessage());
        }
        catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"注册失败");
        }
    }
}
