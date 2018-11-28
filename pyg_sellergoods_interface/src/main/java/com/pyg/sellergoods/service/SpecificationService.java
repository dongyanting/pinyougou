package com.pyg.sellergoods.service;

import com.pyg.pojo.TbSpecification;
import entity.PageResult;
import groupEntity.Specification;

import java.util.List;
import java.util.Map;

public interface SpecificationService {
    public List<TbSpecification> findAll();

    PageResult findPage(int pageNo, int pageSize);

    void add(Specification specification);

    Specification findOne(Long id);

    void update(Specification specification);

    void dele(Long[] ids);

    PageResult findPage(int pageNo, int pageSize, TbSpecification specification);

    List<Map> findSpecList();
}
