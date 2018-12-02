package com.itheima;

import com.alibaba.fastjson.JSON;
import com.pyg.pojo.TbItem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/applicationContext-solr.xml")
public class SolrTest {

    @Autowired
    private SolrTemplate solrTemplate;

    @Test
    public void testAdd() {
        TbItem tbItem = new TbItem();
        tbItem.setId(10L);
        tbItem.setTitle("测试title");
        solrTemplate.saveBean(tbItem);
        solrTemplate.commit();
    }

    @Test
    public void testUpdate() {
        TbItem tbItem = new TbItem();
        tbItem.setId(1L);
        tbItem.setTitle("测试title1111");
        solrTemplate.saveBean(tbItem);
        solrTemplate.commit();
    }

    @Test
    public void testQuery() {
        Query query = new SimpleQuery("item_title:测试");
        ScoredPage<TbItem> scoredPage = solrTemplate.queryForPage(query, TbItem.class);
        List<TbItem> content = scoredPage.getContent();
        for (TbItem tbItem : content) {
            System.out.println(tbItem.getTitle());
        }
    }

    @Test
    public void testDelete() {
//        solrTemplate.deleteById("1");
        SolrDataQuery solrDataQuery = new SimpleQuery("item_title:测试"); // *:*  表示所有
        solrTemplate.delete(solrDataQuery);
        solrTemplate.commit();
    }

    @Test
    public void testHLQuery() {

        // 设置了关键字
        HighlightQuery highlightQuery = new SimpleHighlightQuery(new Criteria("item_title").is("小米"));
        // 设置关于高亮的属性
        HighlightOptions highlightOptions = new HighlightOptions();
        highlightOptions.addField("item_title"); // 高亮的域名
        highlightOptions.setSimplePrefix("<span style=\"color:red\">"); // 前缀
        highlightOptions.setSimplePostfix("</span>");  // 后缀
        highlightQuery.setHighlightOptions(highlightOptions);
        HighlightPage<TbItem> highlightPage = solrTemplate.queryForHighlightPage(highlightQuery, TbItem.class);
        System.out.println(JSON.toJSONString(highlightPage, true));

        List<TbItem> content = highlightPage.getContent();// 当前页的数据
        for (TbItem tbItem : content) {
            List<HighlightEntry.Highlight> highlights = highlightPage.getHighlights(tbItem);
            if (highlights != null && highlights.size() > 0) {
                List<String> snipplets = highlights.get(0).getSnipplets();
                if (snipplets!=null&&snipplets.size()>0) {
                    String title = snipplets.get(0);
                    tbItem.setTitle(title);
                }
            }

            for (TbItem item : content) {
                System.out.println(item.getTitle());
            }

        }


    }

    @Test
    public void testGroupQ() {
        // 根据关键字查询使用springdatasolr分组查询
        Query groupQuery = new SimpleQuery(new Criteria("item_keywords").is("三星")); // 设置关键字 相当于 where title like '%三星%'
        groupQuery.setGroupOptions(new GroupOptions().addGroupByField("item_category")); // 设置分组属性  相当于group by category
        GroupPage<TbItem> groupPage = solrTemplate.queryForGroupPage(groupQuery, TbItem.class);
        GroupResult<TbItem> groupResult = groupPage.getGroupResult("item_category");
        System.out.println(JSON.toJSONString(groupResult,true));
    }

}
