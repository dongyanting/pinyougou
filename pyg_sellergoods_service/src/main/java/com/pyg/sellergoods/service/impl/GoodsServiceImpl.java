package com.pyg.sellergoods.service.impl;
import java.util.*;

import com.alibaba.fastjson.JSON;
import com.pyg.mapper.*;
import com.pyg.pojo.TbGoodsDesc;
import com.pyg.pojo.TbItem;
import groupEntity.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pyg.pojo.TbGoods;
import com.pyg.pojo.TbGoodsExample;
import com.pyg.pojo.TbGoodsExample.Criteria;
import com.pyg.sellergoods.service.GoodsService;

import entity.PageResult;
import org.springframework.transaction.annotation.Transactional;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
@Transactional
public class GoodsServiceImpl implements GoodsService {

	@Autowired
	private TbGoodsMapper goodsMapper;

	@Autowired
	private TbGoodsDescMapper goodsDescMapper;

	@Autowired
	private TbItemMapper itemMapper;

	@Autowired
	private TbItemCatMapper itemCatMapper;

	@Autowired
	private TbBrandMapper brandMapper;

	@Autowired
	private TbSellerMapper sellerMapper;
	/**
	 * 查询全部
	 */
	@Override
	public List<TbGoods> findAll() {
		return goodsMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbGoods> page=   (Page<TbGoods>) goodsMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(Goods goods) {
		// 组合类中包含了三个表 TbGoods TbGoodsDesc TbItem
		TbGoods tbGoods = goods.getTbGoods();
		// 商家ID   当前登录人  写在了GoodsController的add方法里面
		// 状态  0:未审核  1：已审核 2：未通过
		tbGoods.setAuditStatus("0");
		// 是否上架   0:未上架  1：已上架 2：已下架
		tbGoods.setIsMarketable("0");
		// 是否删除   0：未删除   1：已删除
		tbGoods.setIsDelete("0");

		goodsMapper.insert(tbGoods);           // 插入时需要返回id
		TbGoodsDesc tbGoodsDesc = goods.getTbGoodsDesc();
		// SPU_ID  取自tb_goods的id
		tbGoodsDesc.setGoodsId(tbGoods.getId());

		goodsDescMapper.insert(tbGoodsDesc);
		List<TbItem> itemList = goods.getItemList();
		for (TbItem tbItem : itemList) {
			String title = tbGoods.getGoodsName();
			// 商品标题  spu名称+spec中的value值 例如：小米6X 移动4g 32g
			String spec = tbItem.getSpec();  // {"网络":"移动4G","机身内存":"32G"}
			Map<String,String> specMap = JSON.parseObject(spec, Map.class);
//			Set<String> strings = specMap.keySet();
			for (String key : specMap.keySet()) {
				title += " " + specMap.get(key);
			}
			tbItem.setTitle(title);
			// 商品卖点  取自spu的副标题
			tbItem.setSellPoint(tbGoods.getCaption());
			// 商品图片  取自商品图片中的第一个图片
			String itemImages = tbGoodsDesc.getItemImages(); //[{color:,url:}]
			List<Map> itemImageMapList = JSON.parseArray(itemImages, Map.class);
			if (itemImageMapList.size()>0) {
				String url = (String) itemImageMapList.get(0).get("url");
				tbItem.setImage(url);
			}

			// 所属类目  第三极id
			tbItem.setCategoryid(tbGoods.getCategory3Id());
			// 创建时间
			tbItem.setCreateTime(new Date());
			// 更新时间
			tbItem.setUpdateTime(new Date());
			// spuId
			tbItem.setGoodsId(tbGoods.getId());
			// 商家Id
			tbItem.setSellerId(tbGoods.getSellerId());
			// 分类的名称  根据分类id查对象 取名称
			tbItem.setBrand(itemCatMapper.selectByPrimaryKey(tbItem.getCategoryid()).getName());
			// 品牌的名称  根据品牌id查对象 取名称
			tbItem.setCategory(brandMapper.selectByPrimaryKey(tbGoods.getBrandId()).getName());
			// 商家的名称  根据商家id查对象 取名称
			tbItem.setSeller(sellerMapper.selectByPrimaryKey(tbItem.getSellerId()).getName());
			itemMapper.insert(tbItem);
		}

	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbGoods goods){
		goodsMapper.updateByPrimaryKey(goods);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbGoods findOne(Long id){
		return goodsMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			goodsMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbGoodsExample example=new TbGoodsExample();
		Criteria criteria = example.createCriteria();
		
		if(goods!=null){			
						if(goods.getSellerId()!=null && goods.getSellerId().length()>0){
				criteria.andSellerIdEqualTo(goods.getSellerId());
			}
			if(goods.getGoodsName()!=null && goods.getGoodsName().length()>0){
				criteria.andGoodsNameLike("%"+goods.getGoodsName()+"%");
			}
			if(goods.getAuditStatus()!=null && goods.getAuditStatus().length()>0){
				criteria.andAuditStatusLike("%"+goods.getAuditStatus()+"%");
			}
			if(goods.getIsMarketable()!=null && goods.getIsMarketable().length()>0){
				criteria.andIsMarketableLike("%"+goods.getIsMarketable()+"%");
			}
			if(goods.getCaption()!=null && goods.getCaption().length()>0){
				criteria.andCaptionLike("%"+goods.getCaption()+"%");
			}
			if(goods.getSmallPic()!=null && goods.getSmallPic().length()>0){
				criteria.andSmallPicLike("%"+goods.getSmallPic()+"%");
			}
			if(goods.getIsEnableSpec()!=null && goods.getIsEnableSpec().length()>0){
				criteria.andIsEnableSpecLike("%"+goods.getIsEnableSpec()+"%");
			}
			if(goods.getIsDelete()!=null && goods.getIsDelete().length()>0){
				criteria.andIsDeleteLike("%"+goods.getIsDelete()+"%");
			}
	
		}
		
		Page<TbGoods> page= (Page<TbGoods>)goodsMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public void updateAuditStatus(Long[] ids, String auditStatus) {
		for (Long id : ids) {
			Map paraMap = new HashMap();
			paraMap.put("id",id);
			paraMap.put("auditStatus",auditStatus);
			goodsMapper.updateAuditStatus(paraMap);
		}
	}

	@Override
	public void updateIsMarketable(Long[] ids, String market) {
		for (Long id : ids) {
			Map paraMap = new HashMap();
			paraMap.put("id",id);
			paraMap.put("market",market);
			goodsMapper.updateIsMarketable(paraMap);
		}
	}

}
