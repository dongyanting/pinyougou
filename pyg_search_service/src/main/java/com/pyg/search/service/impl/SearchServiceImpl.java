package com.pyg.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.pyg.pojo.TbItem;
import com.pyg.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    private SolrTemplate solrTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Map searchByParam(Map paramMap) {
        Map resultMap = new HashMap();

        Object keyword = paramMap.get("keyword");

//      ---------------------------分组查询开始------------------------------
        List<String> categoryList = new ArrayList<>();
        // 根据关键字查询使用springdatasolr分组查询
        Query groupQuery = new SimpleQuery(new Criteria("item_keywords").is(keyword)); // 设置关键字 相当于 where title like '%三星%'
        groupQuery.setGroupOptions(new GroupOptions().addGroupByField("item_category")); // 设置分组属性  相当于group by category
        GroupPage<TbItem> groupPage = solrTemplate.queryForGroupPage(groupQuery, TbItem.class);
        GroupResult<TbItem> groupResult = groupPage.getGroupResult("item_category");
        Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
        List<GroupEntry<TbItem>> content1 = groupEntries.getContent();
        for (GroupEntry<TbItem> tbItemGroupEntry : content1) {
            categoryList.add(tbItemGroupEntry.getGroupValue());
        }
        resultMap.put("categoryList",categoryList);
//        ---------------------------分组查询结束------------------------------

//        ---------------------------根据分类查询品牌和规格开始------------------------------
        String itemCatName = categoryList.get(0);
        List<Map> brandList = (List<Map>) redisTemplate.boundHashOps("cat_brand").get(itemCatName);
        List<Map> specList = (List<Map>) redisTemplate.boundHashOps("cat_spec").get(itemCatName);
        resultMap.put("brandList",brandList);
        resultMap.put("specList",specList);

//        ---------------------------根据分类查询品牌和规格结束------------------------------


//        ---------------------------根据关键字进行查询开始------------------------------
        // 设置了关键字
        HighlightQuery highlightQuery = new SimpleHighlightQuery(new Criteria("item_keywords").is(keyword));
        // 设置关于高亮的属性
        HighlightOptions highlightOptions = new HighlightOptions();
        highlightOptions.addField("item_title"); // 高亮的域名
        highlightOptions.setSimplePrefix("<span style=\"color:red\">"); // 前缀
        highlightOptions.setSimplePostfix("</span>");  // 后缀
        highlightQuery.setHighlightOptions(highlightOptions);

        // 添加过滤条件
//        分类
        if (!paramMap.get("category").equals("")) {
            highlightQuery.addFilterQuery(new SimpleFilterQuery(new Criteria("item_category").is(paramMap.get("category"))));
        }

//        品牌
        if (!paramMap.get("brand").equals("")) {
            highlightQuery.addFilterQuery(new SimpleFilterQuery(new Criteria("item_brand").is(paramMap.get("brand"))));
        }

        // 规格  "spec":{"网络":"移动3G","机身内存":"32G"}}
        Map<String,String> specMap = (Map<String, String>) paramMap.get("spec");
//        highlightQuery.addFilterQuery(new SimpleFilterQuery(new Criteria("item_brand").is(paramMap.get("brand"))));
        for (String key : specMap.keySet()) {
            highlightQuery.addFilterQuery(new SimpleFilterQuery(new Criteria("item_spec_" + key).is(specMap.get(key))));
        }

        //        价格区间
        if (!paramMap.get("price").equals("")) {
            String[] prices = paramMap.get("price").toString().split("-");

            if (!prices[1].equals("*")) {
                highlightQuery.addFilterQuery(new SimpleFilterQuery(new Criteria("item_price").between(prices[0],prices[1],true,true)));
            } else {
                highlightQuery.addFilterQuery(new SimpleFilterQuery(new Criteria("item_price").greaterThanEqual(prices[0])));
            }

        }

        // 价格排序
        if (paramMap.get("order").equals("asc")) {
            highlightQuery.addSort(new Sort(Sort.Direction.ASC,"item_price"));
        } else {
            highlightQuery.addSort(new Sort(Sort.Direction.DESC,"item_price"));
        }

        // 分页
        Integer pageNo = (Integer) paramMap.get("pageNo");

        highlightQuery.setOffset((pageNo-1)*60);//起始位置
        highlightQuery.setRows(60);//每页显示最大条数





        HighlightPage<TbItem> highlightPage = solrTemplate.queryForHighlightPage(highlightQuery, TbItem.class);
//        System.out.println(JSON.toJSONString(highlightPage, true));

        List<TbItem> content = highlightPage.getContent();// 当前页的数据
        for (TbItem tbItem : content) {
            List<HighlightEntry.Highlight> highlights = highlightPage.getHighlights(tbItem);
            if (highlights != null && highlights.size() > 0) {
                List<String> snipplets = highlights.get(0).getSnipplets();
                if (snipplets != null && snipplets.size() > 0) {
                    String title = snipplets.get(0);
                    tbItem.setTitle(title);
                }
            }

        }

        List<TbItem> itemList = highlightPage.getContent();//当前页的数据
        long total = highlightPage.getTotalElements();//总条数
        int totalPages = highlightPage.getTotalPages();
        resultMap.put("itemList",itemList);
        resultMap.put("total",total);


        resultMap.put("totalPages",totalPages);
        return resultMap;
    }
}
