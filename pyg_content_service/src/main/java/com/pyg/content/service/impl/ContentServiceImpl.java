package com.pyg.content.service.impl;
import java.util.List;

import com.pyg.content.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pyg.mapper.TbContentMapper;
import com.pyg.pojo.TbContent;
import com.pyg.pojo.TbContentExample;
import com.pyg.pojo.TbContentExample.Criteria;


import entity.PageResult;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class ContentServiceImpl implements ContentService {

	@Autowired
	private TbContentMapper contentMapper;
	@Autowired
	private RedisTemplate redisTemplate;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbContent> findAll() {
		return contentMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbContent> page=   (Page<TbContent>) contentMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbContent content) {
		contentMapper.insert(content);
//		删除的是当前分类下的所有广告数据
		redisTemplate.boundHashOps("contentList").delete(content.getCategoryId());
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbContent content){
//		首页轮播类型----》今日推荐
//		content.getCategoryId(); //页面传入的值 今日推荐的id
		TbContent tbContent = contentMapper.selectByPrimaryKey(content.getId());//修改之前的对象
//		tbContent.getCategoryId(); //修改之前的 首页轮播类型id
		contentMapper.updateByPrimaryKey(content);
		redisTemplate.boundHashOps("contentList").delete(content.getCategoryId());

		if(tbContent.getCategoryId().longValue()!=content.getCategoryId().longValue()){
			redisTemplate.boundHashOps("contentList").delete(tbContent.getCategoryId());
		}
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbContent findOne(Long id){
		return contentMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			contentMapper.deleteByPrimaryKey(id);
			// 根据id查询对象 目的：根据对象获取分类id
			TbContent content = contentMapper.selectByPrimaryKey(id);
			redisTemplate.boundHashOps("contentList").delete(content.getCategoryId());
		}		
	}
	
	
		@Override
	public PageResult findPage(TbContent content, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbContentExample example=new TbContentExample();
		Criteria criteria = example.createCriteria();
		
		if(content!=null){			
						if(content.getTitle()!=null && content.getTitle().length()>0){
				criteria.andTitleLike("%"+content.getTitle()+"%");
			}
			if(content.getUrl()!=null && content.getUrl().length()>0){
				criteria.andUrlLike("%"+content.getUrl()+"%");
			}
			if(content.getPic()!=null && content.getPic().length()>0){
				criteria.andPicLike("%"+content.getPic()+"%");
			}
			if(content.getStatus()!=null && content.getStatus().length()>0){
				criteria.andStatusLike("%"+content.getStatus()+"%");
			}
	
		}
		
		Page<TbContent> page= (Page<TbContent>)contentMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public List<TbContent> findByCategotyId(Long cid) {

		List<TbContent> contentList = (List<TbContent>) redisTemplate.boundHashOps("contentList").get(cid);
		if(contentList==null){
			//		select * from tb_content where category_id=? and status=1 order by sort_order
			TbContentExample example = new TbContentExample();
			example.createCriteria().andCategoryIdEqualTo(cid).andStatusEqualTo("1");
			example.setOrderByClause("sort_order"); //设置排序"sort_order" "sortOrder"
			contentList = contentMapper.selectByExample(example);
			redisTemplate.boundHashOps("contentList").put(cid,contentList);

			System.out.println("from MYSQL");
		}else {
			System.out.println("from REDIS");
		}
		return contentList;
	}
}
