package com.qingcheng.controller.goods;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.entity.PageResult;
import com.qingcheng.entity.Result;
import com.qingcheng.pojo.goods.Goods;
import com.qingcheng.pojo.goods.Spu;
import com.qingcheng.service.goods.SpuService;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/spu")
public class SpuController {

    @Reference
    private SpuService spuService;

    @GetMapping("/findAll")
    public List<Spu> findAll(){
        return spuService.findAll();
    }

    @GetMapping("/findPage")
    public PageResult<Spu> findPage(int page, int size){
        return spuService.findPage(page, size);
    }

    @PostMapping("/findList")
    public List<Spu> findList(@RequestBody Map<String,Object> searchMap){
        return spuService.findList(searchMap);
    }

    @PostMapping("/findPage")
    public PageResult<Spu> findPage(@RequestBody Map<String,Object> searchMap,int page, int size){
        return  spuService.findPage(searchMap,page,size);
    }

    @GetMapping("/findById")
    public Spu findById(String id){
        return spuService.findById(id);
    }


    @PostMapping("/add")
    public Result add(@RequestBody Spu spu){
        spuService.add(spu);
        return new Result();
    }

    @PostMapping("/update")
    public Result update(@RequestBody Spu spu){
        spuService.update(spu);
        return new Result();
    }

    @GetMapping("/delete")
    public Result delete(String id){
        spuService.delete(id);
        return new Result();
    }

    /*
    spu与sku列表的保存:
     */
    @PostMapping("/sava")
    public Result sava(@RequestBody Goods goods){
        spuService.savaGoods(goods);
        return new Result();
    }

    /*
   通过spuId查询spu对象和sku列表
    */
    @GetMapping("findGoodsById")
    public Goods findGoodsById(String id) {
        Goods goods = spuService.findGoodsById(id);
        return goods;
    }

    /*
    审核商品 如果审核通过就上架
     */
    @PostMapping("/audit")
    public Result audit(@RequestBody Map<String,String> map){
        spuService.audit(map.get("id"),map.get("status"),map.get("message"));
        return new Result();
    }

    /*
    商品下架  把状态改为0
     */
    @GetMapping("/pull")
    public Result pull(String id){
        spuService.pull(id);
        return new Result();
    }

    /*
        商品上架 上架之前要判断该商品是否审核通过
     */
    @GetMapping("/put")
    public Result put(String id) {
        spuService.put(id);
        return new Result();
    }

    /*
      商品批量上架
   */
    @GetMapping("/putMany")
    public Result putMany(String [] ids) {
        int count = spuService.putMany(ids);
        return new Result(0,"共上架"+count+"件商品");
    }

    /*
      商品批量下架
   */
    @GetMapping("/pullMany")
    public Result pullMany(String [] ids) {
        int count = spuService.pullMany(ids);
        return new Result(0,"共下架"+count+"件商品");
    }




}
