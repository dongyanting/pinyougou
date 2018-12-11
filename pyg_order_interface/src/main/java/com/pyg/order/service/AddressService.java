package com.pyg.order.service;

import com.pyg.pojo.TbAddress;

import java.util.List;

public interface AddressService {
    List<TbAddress> findAddressListByUserId(String userId);
}
