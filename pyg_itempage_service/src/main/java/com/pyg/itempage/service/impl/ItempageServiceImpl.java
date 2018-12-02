package com.pyg.itempage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pyg.itempage.service.ItempageService;
import com.pyg.mapper.TbGoodsDescMapper;
import com.pyg.mapper.TbGoodsMapper;
import com.pyg.mapper.TbItemMapper;
import com.pyg.pojo.TbGoods;
import com.pyg.pojo.TbGoodsDesc;
import com.pyg.pojo.TbItem;
import com.pyg.pojo.TbItemExample;
import groupEntity.Goods;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Service
public class ItempageServiceImpl implements ItempageService {

    @Autowired
    private TbGoodsMapper goodsMapper;

    @Autowired
    private TbGoodsDescMapper goodsDescMapper;

    @Autowired
    private TbItemMapper itemMapper;

    @Override
    public Goods findOne(Long goodsId) {
        Goods goods = new Goods();
//        goodsId 是 tb_goods   tb_goods_desc 中的id
        TbGoods tbGoods = goodsMapper.selectByPrimaryKey(goodsId);
        TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);

        TbItemExample example = new TbItemExample();
        example.createCriteria().andGoodsIdEqualTo(goodsId);
        List<TbItem> itemList = itemMapper.selectByExample(example);

        goods.setTbGoods(tbGoods);
        goods.setTbGoodsDesc(tbGoodsDesc);
        goods.setItemList(itemList);
        return goods;
    }

    // 获取组合类的集合

    @Override
    public List<Goods> findAll() {
//        goods--->tb_goods  一对一
        List<Goods> goodsList = new ArrayList<>();
        List<TbGoods> tbGoodsList = goodsMapper.selectByExample(null);
        for (TbGoods tbGoods : tbGoodsList) {
            Goods goods = findOne(tbGoods.getId());
            goodsList.add(goods);
        }
        return goodsList;
    }
}
