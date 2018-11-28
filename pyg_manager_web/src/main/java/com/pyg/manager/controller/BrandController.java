package com.pyg.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pyg.pojo.TbBrand;
import com.pyg.sellergoods.service.BrandService;
import entity.PageResult;
import entity.Result;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

//@Controller
//@ResponseBody    //1、转json  2、回显到浏览器
@RestController
@RequestMapping("/brand")
public class BrandController {

    @Reference
    private BrandService brandService;
    //此方法是添加模板时需要的 要求数据返回的格式是：[{"id":1,"text":"联想"},{"id":2,"text":"华为"}]
    @RequestMapping("/findBrandList")
    public List<Map> findBrandList(){
      return   brandService.findBrandList();
    }


    @RequestMapping("/findAll")
    public List<TbBrand> findAll(){
      return   brandService.findAll();
    }


    @RequestMapping("/findPage")
    public PageResult findPage(int pageNo, int pageSize){
//        response的格式{total:100,rows:[{},{},{}]}
      return   brandService.findPage(pageNo,pageSize);
    }
    @RequestMapping("/add")
    public Result add(@RequestBody TbBrand brand){ //@RequestBody以json、数据格式接收参数
//        response的格式{success:true|false,message:"保存成功"|"保存失败"}
        try {
            brandService.add(brand);
            return new Result(true,"保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"保存失败");
        }
    }


    @RequestMapping("/update")
    public Result update(@RequestBody TbBrand brand){ //@RequestBody以json、数据格式接收参数
//        response的格式{success:true|false,message:"修改成功"|"修改失败"}
        try {
            brandService.update(brand);
            return new Result(true,"修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"修改失败");
        }
    }
    @RequestMapping("/findOne")
    public TbBrand findOne(Long id){
        return   brandService.findOne(id);
    }

    @RequestMapping("/dele")
    public Result dele(Long[] ids){ //@RequestBody以json、数据格式接收参数
        try {
            brandService.dele(ids);
            return new Result(true,"删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"删除失败");
        }
    }


    @RequestMapping("/search")
    public PageResult search(int pageNo, int pageSize,@RequestBody TbBrand brand){
//        response的格式{total:100,rows:[{},{},{}]}
        return   brandService.findPage(pageNo,pageSize,brand);
    }
}
