package com.pyg.data.inti;

import com.alibaba.fastjson.JSON;
import com.pyg.mapper.TbItemMapper;
import com.pyg.pojo.TbItem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:spring/applicationContext*.xml")
public class SolrManager {
    @Autowired
    private TbItemMapper itemMapper;
    @Autowired
    private SolrTemplate solrTemplate;
    //    初始化solr数据
//    select i.* from tb_goods g,tb_item i where i.goods_id=g.id and g.is_marketable='1'
    @Test
    public void initSolr(){
        List<TbItem> itemList = itemMapper.grounding();
        for (TbItem tbItem : itemList) {
            String spec = tbItem.getSpec();//{"网络":"移动4G","机身内存":"64G"}
            Map<String,String> map = JSON.parseObject(spec, Map.class);
            tbItem.setSpecMap(map);
        }
        solrTemplate.saveBeans(itemList);
        solrTemplate.commit();
    }

}
