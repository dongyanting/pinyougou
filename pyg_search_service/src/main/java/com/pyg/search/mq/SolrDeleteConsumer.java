package com.pyg.search.mq;

import com.alibaba.fastjson.JSON;
import com.pyg.mapper.TbItemMapper;
import com.pyg.pojo.TbItem;
import com.pyg.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.SolrDataQuery;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.List;
import java.util.Map;

public class SolrDeleteConsumer implements MessageListener {

    @Autowired
    private SolrTemplate solrTemplate;


    @Override
    public void onMessage(Message message) {
        TextMessage textMessage = (TextMessage) message;
        try {
            String goodsId = textMessage.getText();
            SolrDataQuery query = new SimpleQuery("item_goodsid" + goodsId);
            solrTemplate.delete(query);
            solrTemplate.commit();

            System.out.println("solrDelete is success!!!");
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
