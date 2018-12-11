package com.pyg.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pyg.mapper.TbAddressMapper;
import com.pyg.order.service.AddressService;
import com.pyg.pojo.TbAddress;
import com.pyg.pojo.TbAddressExample;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private TbAddressMapper addressMapper;

    @Override
    public List<TbAddress> findAddressListByUserId(String userId) {

        TbAddressExample example = new TbAddressExample();
        example.createCriteria().andUserIdEqualTo(userId);
        return addressMapper.selectByExample(example);

    }
}
