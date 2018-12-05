package com.pyg.search.mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pyg.mapper.TbItemMapper;
import com.pyg.pojo.TbItem;
import com.pyg.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.List;
import java.util.Map;

public class SolrUpdateConsumer implements MessageListener {

    @Autowired
    private SolrTemplate solrTemplate;

    @Autowired
    private TbItemMapper itemMapper;

    @Override
    public void onMessage(Message message) {
        TextMessage textMessage = (TextMessage) message;
        try {
            String goodsId = textMessage.getText();
            TbItemExample example = new TbItemExample();
            example.createCriteria().andGoodsIdEqualTo(Long.parseLong(goodsId));
            List<TbItem> itemList = itemMapper.selectByExample(example);
            // 动态域赋值
            for (TbItem tbItem : itemList) {
                String spec = tbItem.getSpec();
                Map<String,String> map = JSON.parseObject(spec,Map.class);
                tbItem.setSpecMap(map);
            }
            solrTemplate.saveBeans(itemList);
            solrTemplate.commit();
            System.out.println("solrUpdate is success!!!");

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
