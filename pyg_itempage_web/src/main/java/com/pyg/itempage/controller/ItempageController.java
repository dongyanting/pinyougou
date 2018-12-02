package com.pyg.itempage.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.pyg.itempage.service.ItempageService;
import com.pyg.pojo.TbItem;
import freemarker.template.Template;
import groupEntity.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import freemarker.template.Configuration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/itempage")
public class ItempageController {

    @Reference
    private ItempageService itempageService;

    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;

    @RequestMapping("/gen_item")
    public String generatorItem(Long goodsId) throws Exception{

        // 根据goodsId获取一个组合类
        Goods goods = itempageService.findOne(goodsId);

        Configuration configuration = freeMarkerConfigurer.getConfiguration();
        Template template = configuration.getTemplate("item.ftl");

        List<TbItem> itemList = goods.getItemList();
        for (TbItem tbItem : itemList) {
            Map modelData = new HashMap();
            modelData.put("goods",goods);
            modelData.put("tbItem",tbItem);
            Writer writer = new FileWriter("D:\\class69\\html\\"+tbItem.getId()+".html");
            template.process(modelData,writer);
            writer.close();
        }


        return "SUCCESS";
    }



    @RequestMapping("/gen_itemAll")
    public String generatorItemAll() throws Exception{

        // 根据goodsId获取一个组合类
        List<Goods> goodsList = itempageService.findAll();
        for (Goods goods : goodsList) {
            Configuration configuration = freeMarkerConfigurer.getConfiguration();
            Template template = configuration.getTemplate("item.ftl");

            List<TbItem> itemList = goods.getItemList();
            for (TbItem tbItem : itemList) {
                Map modelData = new HashMap();
                modelData.put("goods",goods);
                modelData.put("tbItem",tbItem);
                Writer writer = new FileWriter("D:\\class69\\html\\"+tbItem.getId()+".html");
                template.process(modelData,writer);
                writer.close();
            }
        }
        return "SUCCESS";
    }
}
