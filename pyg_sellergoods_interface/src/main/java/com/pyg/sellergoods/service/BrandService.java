package com.pyg.sellergoods.service;

import com.pyg.pojo.TbBrand;
import entity.PageResult;

import java.util.List;
import java.util.Map;

public interface BrandService {
    public List<TbBrand> findAll();

    PageResult findPage(int pageNo, int pageSize);

    void add(TbBrand brand);

    TbBrand findOne(Long id);

    void update(TbBrand brand);

    void dele(Long[] ids);

    PageResult findPage(int pageNo, int pageSize, TbBrand brand);

    List<Map> findBrandList();
}
