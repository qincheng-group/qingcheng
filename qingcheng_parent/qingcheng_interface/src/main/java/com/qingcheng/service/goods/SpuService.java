package com.qingcheng.service.goods;
import com.qingcheng.entity.PageResult;
import com.qingcheng.pojo.goods.Goods;
import com.qingcheng.pojo.goods.Spu;

import java.util.*;

/**
 * spu业务逻辑层
 */
public interface SpuService {

    public List<Spu> findAll();

    public PageResult<Spu> findPage(int page, int size);

    public List<Spu> findList(Map<String,Object> searchMap);

    public PageResult<Spu> findPage(Map<String,Object> searchMap,int page, int size);

    public Spu findById(String id);

    public void add(Spu spu);

    public void update(Spu spu);

    public void delete(String id);

    /*
    保存sku和spu的组合实体类
     */
    public void savaGoods(Goods good);

    /*
    通过spuId查询spu对象和sku列表
     */
    public Goods findGoodsById(String id);

    /*
    商品审核 通过审核后上架
     */
    public void audit(String id,String status,String message);

    /*
    商品下架
     */
    public void pull(String id);

    /*
    商品上架 ：
     */
    public void put(String id);

    /*
    批量上架
     */
    public int putMany(String [] ids);

    /*
    批量下架
     */
    public int pullMany(String [] ids);
}