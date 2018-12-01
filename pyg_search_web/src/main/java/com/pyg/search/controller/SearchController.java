package com.pyg.search.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.pyg.search.service.SearchService;
import java.util.Map;

@RestController
@RequestMapping("/search")
public class SearchController {

    @Reference
    private SearchService searchService;

    @RequestMapping("/searchByParam")
    public Map searchByParam(@RequestBody Map paramMap){
        return searchService.searchByParam(paramMap);
    }
}

