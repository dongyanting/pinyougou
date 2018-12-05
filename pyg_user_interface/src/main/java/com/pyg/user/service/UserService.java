package com.pyg.user.service;

import com.pyg.pojo.TbUser;

public interface UserService {
    void register(TbUser user, String code);

    void sendSms(String phone)throws Exception;
}
