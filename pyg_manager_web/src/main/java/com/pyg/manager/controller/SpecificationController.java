package com.pyg.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pyg.pojo.TbSpecification;
import com.pyg.sellergoods.service.SpecificationService;
import entity.PageResult;
import entity.Result;
import groupEntity.Specification;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/specification")
public class SpecificationController {

    @Reference
    private SpecificationService specificationService;

    @RequestMapping("/findSpecList")
    public List<Map> findSpecList(){
      return   specificationService.findSpecList();
    }
    @RequestMapping("/findAll")
    public List<TbSpecification> findAll(){
      return   specificationService.findAll();
    }


    @RequestMapping("/findPage")
    public PageResult findPage(int pageNo, int pageSize){
//        response的格式{total:100,rows:[{},{},{}]}
      return   specificationService.findPage(pageNo,pageSize);
    }
    @RequestMapping("/add")
    public Result add(@RequestBody Specification specification){ //@RequestBody以json、数据格式接收参数
//        response的格式{success:true|false,message:"保存成功"|"保存失败"}
        try {
            specificationService.add(specification);
            return new Result(true,"保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"保存失败");
        }
    }


    @RequestMapping("/update")
    public Result update(@RequestBody Specification specification){ //@RequestBody以json、数据格式接收参数
//        response的格式{success:true|false,message:"修改成功"|"修改失败"}
        try {
            specificationService.update(specification);
            return new Result(true,"修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"修改失败");
        }
    }
    @RequestMapping("/findOne")
    public Specification findOne(Long id){
        return   specificationService.findOne(id);
    }

    @RequestMapping("/dele")
    public Result dele(Long[] ids){ //@RequestBody以json、数据格式接收参数
        try {
            specificationService.dele(ids);
            return new Result(true,"删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"删除失败");
        }
    }


    @RequestMapping("/search")
    public PageResult search(int pageNo, int pageSize,@RequestBody TbSpecification specification){
//        response的格式{total:100,rows:[{},{},{}]}
        return   specificationService.findPage(pageNo,pageSize,specification);
    }
}
