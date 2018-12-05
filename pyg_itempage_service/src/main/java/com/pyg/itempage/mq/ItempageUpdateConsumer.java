package com.pyg.itempage.mq;

import com.pyg.itempage.service.ItempageService;
import com.pyg.pojo.TbItem;
import freemarker.template.Configuration;
import freemarker.template.Template;
import groupEntity.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItempageUpdateConsumer implements MessageListener {

    @Autowired
    private ItempageService itempageService;

    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;

    @Override
    public void onMessage(Message message) {
        TextMessage textMessage = (TextMessage) message;
        try {
            String goodsId = textMessage.getText();

            // 根据goodsId获取一个组合类
            Goods goods = itempageService.findOne(Long.parseLong(goodsId));

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
            System.out.println("itempageUpdate is success!!!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
