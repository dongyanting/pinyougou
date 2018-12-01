package com.itheima;

import com.pyg.pojo.TbItem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.SolrDataQuery;
import org.springframework.data.solr.core.query.result.ScoredPage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/applicationContext-solr.xml")
public class SolrTest {

    @Autowired
    private SolrTemplate solrTemplate;

    @Test
    public void testAdd(){
        TbItem tbItem = new TbItem();
        tbItem.setId(10L);
        tbItem.setTitle("测试title");
        solrTemplate.saveBean(tbItem);
        solrTemplate.commit();
    }

    @Test
    public void testUpdate(){
        TbItem tbItem = new TbItem();
        tbItem.setId(1L);
        tbItem.setTitle("测试title1111");
        solrTemplate.saveBean(tbItem);
        solrTemplate.commit();
    }

    @Test
    public void testQuery(){
        Query query = new SimpleQuery("item_title:测试");
        ScoredPage<TbItem> scoredPage = solrTemplate.queryForPage(query, TbItem.class);
        List<TbItem> content = scoredPage.getContent();
        for (TbItem tbItem : content) {
            System.out.println(tbItem.getTitle());
        }
    }

    @Test
    public void testDelete(){
//        solrTemplate.deleteById("1");
        SolrDataQuery solrDataQuery = new SimpleQuery("item_title:测试"); // *:*  表示所有
        solrTemplate.delete(solrDataQuery);
        solrTemplate.commit();
    }

}
