package com.pyg.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pyg.mapper.TbUserMapper;
import com.pyg.pojo.TbUser;
import com.pyg.user.service.UserService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import utils.HttpClient;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private TbUserMapper userMapper;

    @Override
    public void register(TbUser user, String code) {
        String numeric = (String) redisTemplate.boundValueOps("sms_" + user.getPhone()).get();
        if (numeric == null) {
            throw new RuntimeException("验证码已经失效");
        }
        if (!numeric.equals(code)) {
            throw new RuntimeException("验证码输入有误");
        }

        String password = user.getPassword(); // 明文密码
        password = DigestUtils.md5Hex(password);
        user.setPassword(password);
        user.setCreated(new Date());
        user.setUpdated(new Date());

        userMapper.insert(user);

        // 验证码使用
        redisTemplate.delete("sms_" + user.getPhone());

    }

    @Override
    public void sendSms(String phone) throws Exception {

        //
        HttpClient httpClient = new HttpClient("http://127.0.0.1:7788/sms/sendSms");
        httpClient.addParameter("phoneNumber",phone);
        httpClient.addParameter("signName","品味优雅购物");
        httpClient.addParameter("templateCode","SMS_130926832");

        String numeric = RandomStringUtils.randomNumeric(4);

        System.out.println(numeric);

        httpClient.addParameter("templateParam","{\"code\":\""+numeric+"\"}");
        httpClient.post();
        System.out.println(httpClient.getContent());

        // 把验证码放入Redis中  30s过期
        redisTemplate.boundValueOps("sms_" + phone).set(numeric,30, TimeUnit.SECONDS);
    }
}
