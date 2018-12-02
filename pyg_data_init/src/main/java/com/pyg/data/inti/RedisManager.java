package com.pyg.data.inti;

import com.alibaba.fastjson.JSON;
import com.pyg.mapper.TbItemCatMapper;
import com.pyg.mapper.TbItemMapper;
import com.pyg.mapper.TbSpecificationOptionMapper;
import com.pyg.mapper.TbTypeTemplateMapper;
import com.pyg.pojo.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:spring/applicationContext*.xml")
public class RedisManager {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private TbItemCatMapper itemCatMapper;

    @Autowired
    private TbTypeTemplateMapper typeTemplateMapper;

    @Autowired
    private TbSpecificationOptionMapper specificationOptionMapper;

    @Test
    public void initRedis(){
        List<TbItemCat> itemCatList = itemCatMapper.selectByExample(null);
        for (TbItemCat tbItemCat : itemCatList) {
            Long typeId = tbItemCat.getTypeId();
            TbTypeTemplate tbTypeTemplate = typeTemplateMapper.selectByPrimaryKey(typeId);
            String brandIds = tbTypeTemplate.getBrandIds();
            List<Map> brandMaps = JSON.parseArray(brandIds, Map.class);

            // 初始化分类名称和品牌列表数据
            redisTemplate.boundHashOps("cat_brand").put(tbItemCat.getName(),brandMaps);

            // 初始化分类名称和规格列表数据
            String specIds = tbTypeTemplate.getSpecIds();  // [{"id":27,"text":"网络"},{"id":32,"text":"机身内存"}]

            List<Map> specMaps = JSON.parseArray(specIds, Map.class);
            for (Map specMap : specMaps) {
                TbSpecificationOptionExample example = new TbSpecificationOptionExample();
                example.createCriteria().andSpecIdEqualTo(Long.parseLong(specMap.get("id")+""));
                List<TbSpecificationOption> options = specificationOptionMapper.selectByExample(example);
                specMap.put("options",options);
            }

            redisTemplate.boundHashOps("cat_spec").put(tbItemCat.getName(),specMaps);



        }
    }

}
