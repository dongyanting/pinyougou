package com.pyg.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pyg.pojo.TbItem;
import com.pyg.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.ScoredPage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    private SolrTemplate solrTemplate;   // 从索引库中寻找

    @Override
    public Map searchByParam(Map paramMap) {

        Map resultMap = new HashMap();

        Object keyword = paramMap.get("keyword");
        Query query = new SimpleQuery("item_title:" + keyword);
        ScoredPage<TbItem> scorePage = solrTemplate.queryForPage(query, TbItem.class);
        List<TbItem> itemList = scorePage.getContent(); // 当前页的数据
        long total = scorePage.getTotalElements();
        resultMap.put("itemList",itemList);
        resultMap.put("total",total);
        return null;
    }
}
