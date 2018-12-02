package com.pyg.itempage.service;

import groupEntity.Goods;

import java.util.List;

public interface ItempageService {
    Goods findOne(Long goodsId);

    List<Goods> findAll();
}
