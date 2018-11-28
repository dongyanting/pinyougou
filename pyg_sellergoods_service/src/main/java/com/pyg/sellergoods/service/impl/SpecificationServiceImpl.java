package com.pyg.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pyg.mapper.TbSpecificationMapper;
import com.pyg.mapper.TbSpecificationOptionMapper;
import com.pyg.pojo.TbSpecification;
import com.pyg.pojo.TbSpecificationExample;
import com.pyg.pojo.TbSpecificationOption;
import com.pyg.pojo.TbSpecificationOptionExample;
import com.pyg.sellergoods.service.SpecificationService;
import entity.PageResult;
import groupEntity.Specification;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class SpecificationServiceImpl implements SpecificationService {

    @Autowired
    private TbSpecificationMapper specificationMapper;
    @Autowired
    private TbSpecificationOptionMapper specificationOptionMapper;
    @Override
    public List<TbSpecification> findAll() {
        return specificationMapper.selectByExample(null);
    }

    @Override
    public PageResult findPage(int pageNo, int pageSize) {
        PageHelper.startPage(pageNo,pageSize);
        Page page = (Page) specificationMapper.selectByExample(null);
        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public void add(Specification specification) {
//        specification：组合类
        TbSpecification tbSpecification = specification.getTbSpecification();
        specificationMapper.insert(tbSpecification); //主表    注意：插入数据时返回id
        List<TbSpecificationOption> tbSpecificationOptionList = specification.getTbSpecificationOptionList();
        for (TbSpecificationOption tbSpecificationOption : tbSpecificationOptionList) {
            //            设置外键：
            tbSpecificationOption.setSpecId(tbSpecification.getId());
            specificationOptionMapper.insert(tbSpecificationOption);  // 从表
        }
    }

    @Override
    public Specification findOne(Long id) {
        TbSpecification tbSpecification = specificationMapper.selectByPrimaryKey(id);  //查询主表
//        select * from tb_specifiction_option where spec_id=?
        TbSpecificationOptionExample example = new TbSpecificationOptionExample();
        example.createCriteria().andSpecIdEqualTo(id);
        List<TbSpecificationOption> tbSpecificationOptionList = specificationOptionMapper.selectByExample(example); //查询从表

        Specification specification = new Specification();  //把查询到的数据放到组合类中
        specification.setTbSpecification(tbSpecification);
        specification.setTbSpecificationOptionList(tbSpecificationOptionList);
        return specification;
    }

    @Override
    public void update(Specification specification) {
        TbSpecification tbSpecification = specification.getTbSpecification();
        specificationMapper.updateByPrimaryKey(tbSpecification);

//        根据规格id删除所有此规格下的的规格小项
//        delete from tb_specification_option where spec_id=?
        TbSpecificationOptionExample example = new TbSpecificationOptionExample();
        example.createCriteria().andSpecIdEqualTo(tbSpecification.getId());
        specificationOptionMapper.deleteByExample(example);

//        然后再重新新增
        List<TbSpecificationOption> tbSpecificationOptionList = specification.getTbSpecificationOptionList();
        for (TbSpecificationOption tbSpecificationOption : tbSpecificationOptionList) {
            tbSpecificationOption.setSpecId(tbSpecification.getId()); //注意还需要重新设置规格id
            specificationOptionMapper.insert(tbSpecificationOption);
        }
    }

    @Override
    public void dele(Long[] ids) {
        for (Long id : ids) {
            specificationMapper.deleteByPrimaryKey(id); //只是删除主表  delete from tb_specification where id=?
            //只是删除主表  delete from tb_specification_option where spec_id=?
            TbSpecificationOptionExample example = new TbSpecificationOptionExample();
            example.createCriteria().andSpecIdEqualTo(id);
            specificationOptionMapper.deleteByExample(example);
        }
    }

    @Override
    public PageResult findPage(int pageNo, int pageSize, TbSpecification specification) {
        PageHelper.startPage(pageNo,pageSize);

        TbSpecificationExample example = new TbSpecificationExample();
        TbSpecificationExample.Criteria criteria = example.createCriteria();
         

        Page<TbSpecification> page = (Page<TbSpecification>) specificationMapper.selectByExample(example);
        return new PageResult(page.getTotal(),page.getResult());

    }

    @Override
    public List<Map> findSpecList() {
        return specificationMapper.findSpecList();
    }
}
