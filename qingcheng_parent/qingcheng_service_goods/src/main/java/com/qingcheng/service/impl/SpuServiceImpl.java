package com.qingcheng.service.impl;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.qingcheng.dao.CategoryBrandMapper;
import com.qingcheng.dao.CategoryMapper;
import com.qingcheng.dao.SkuMapper;
import com.qingcheng.dao.SpuMapper;
import com.qingcheng.entity.PageResult;
import com.qingcheng.pojo.goods.*;
import com.qingcheng.service.goods.SpuService;
import com.qingcheng.util.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service(interfaceClass = SpuService.class)
public class SpuServiceImpl implements SpuService {

    @Autowired
    private SpuMapper spuMapper;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private CategoryBrandMapper categoryBrandMapper;


    /**
     * 返回全部记录
     * @return
     */
    public List<Spu> findAll() {
        return spuMapper.selectAll();
    }

    /**
     * 分页查询
     * @param page 页码
     * @param size 每页记录数
     * @return 分页结果
     */
    public PageResult<Spu> findPage(int page, int size) {
        PageHelper.startPage(page,size);
        Page<Spu> spus = (Page<Spu>) spuMapper.selectAll();
        return new PageResult<Spu>(spus.getTotal(),spus.getResult());
    }

    /**
     * 条件查询
     * @param searchMap 查询条件
     * @return
     */
    public List<Spu> findList(Map<String, Object> searchMap) {
        Example example = createExample(searchMap);
        return spuMapper.selectByExample(example);
    }

    /**
     * 分页+条件查询
     * @param searchMap
     * @param page
     * @param size
     * @return
     */
    public PageResult<Spu> findPage(Map<String, Object> searchMap, int page, int size) {
        PageHelper.startPage(page,size);
        Example example = createExample(searchMap);
        Page<Spu> spus = (Page<Spu>) spuMapper.selectByExample(example);
        return new PageResult<Spu>(spus.getTotal(),spus.getResult());
    }

    /**
     * 根据Id查询
     * @param id
     * @return
     */
    public Spu findById(String id) {
        return spuMapper.selectByPrimaryKey(id);
    }

    /**
     * 新增
     * @param spu
     */
    public void add(Spu spu) {
        spuMapper.insert(spu);
    }

    /**
     * 修改
     * @param spu
     */
    public void update(Spu spu) {
        spuMapper.updateByPrimaryKeySelective(spu);
    }

    /**
     *  删除
     * @param id
     */
    public void  delete(String id) {
        spuMapper.deleteByPrimaryKey(id);
    }

    /*

    新增和修改公用一个方法

    保存商品：Sku和spu的组合实体类    spu与sku之间是一对多关系
     */
    @Transactional
    public void savaGoods(Goods goods) {

        //保存一个spu信息
        Spu spu = goods.getSpu();
        if (spu.getId()==null){         //新增
            spu.setId(idWorker.nextId()+"");
            spuMapper.insert(spu);
        }else {  //修改
            //删除原来的sku的list
            Example example = new Example(Sku.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("spuId",spu.getId());
            skuMapper.deleteByExample(example);

            //执行修改操作
            spuMapper.updateByPrimaryKeySelective(spu);

        }



        Date date = new Date();

        //得到分类对象  用来获取分类的名称
        Category category = categoryMapper.selectByPrimaryKey(spu.getCategory3Id());
        //保存多个sku信息
        List<Sku> skuList = goods.getSkuList();

        for (Sku sku : skuList) {
            if(sku.getId()==null){  //增加
                sku.setId(idWorker.nextId()+"");
                sku.setCreateTime(date);       //创建日期
            }

            //设置sku的spu_id为spuid spu与sku之间是一对多关系
            sku.setSpuId(spu.getId());

            String name = sku.getName();


            //获取商品的所有规格
            String skuSpec = sku.getSpec();

            //处理一个商品没有规格的情况
            if (skuSpec==null||"".equals(skuSpec)){
                sku.setSpec("{}");
            }

            //将所有规格转换为一个map格式  方便取得value值
            Map<String,String> specMap = JSON.parseObject(skuSpec, Map.class);

            //修改sku的商品名称为商品拼接每个规格
            for (String value:specMap.values()){
               // name = name+" "+value;
                name+=" "+value;
            }

            sku.setName(name);         //名称
            sku.setUpdateTime(date);       //修改日期
            sku.setCategoryId(spu.getCategory3Id());  // 分类id 商品对应的id是spu的第三级分类 第三级分类才是具体到商品的
            sku.setCategoryName(category.getName());  //分类名称
            sku.setCommentNum(0);           //评论数量
            sku.setSaleNum(0);              //销售数量

            skuMapper.insert(sku);
        }

        //建立分类和品牌管理的关系  （往联合主键表中添加数据）

        CategoryBrand categoryBrand = new CategoryBrand();
        categoryBrand.setBrandId(spu.getBrandId());            //插入品牌的id
        categoryBrand.setCategoryId(spu.getCategory3Id());      //插入分类id

        //判断数据库中是否有该条记录
        int count = categoryBrandMapper.selectCount(categoryBrand);

        if (count==0){
            categoryBrandMapper.insert(categoryBrand);
        }
    }

    /*
    通过spuId查询spu对象和sku列表
     */
    @Override
    public Goods findGoodsById(String id) {
        //查询spu
        Spu spu = spuMapper.selectByPrimaryKey(id);
        //查询sku列表
        Example example = new Example(Sku.class);

        Example.Criteria criteria = example.createCriteria();

        //封装查询条件  按照spuId查询
        criteria.andEqualTo("spuId",id);
        List<Sku> skuList = skuMapper.selectByExample(example);

        //封装到Goods组合实体类中
        Goods goods = new Goods();
        goods.setSpu(spu);
        goods.setSkuList(skuList);

        return goods;
    }

    /*
    商品审核：
        1.修改状态
        2.判断审核是否通过
        3.通过则上架商品
     */

    @Transactional
    @Override
    public void audit(String id, String status, String message) {

        //更新状态
        /*
        效率太低：不如直接实例化一个spu对象  再数据库中直接去改状态
        Spu spu = spuMapper.selectByPrimaryKey(id);
        spu.setStatus(status);
        spuMapper.updateByPrimaryKey(spu);
         */

        Spu  spu= new Spu();
        spu.setStatus(status);
        spu.setId(id);
        if ("1".equals(status)){
            //审核通过,修改自动上架
            spu.setIsMarketable("1");
        }
        spuMapper.updateByPrimaryKeySelective(spu);
    }

    /*
    商品下架
     */
    @Override
    public void pull(String id) {
        Spu spu = new Spu();
        spu.setIsMarketable("0");
        spu.setId(id);
        spuMapper.updateByPrimaryKeySelective(spu);
    }

    /*
    商品上架 上架之前要判断该商品是否审核通过
     */
    @Override
    public void put(String id) {
        if (!"1".equals(spuMapper.selectByPrimaryKey(id).getStatus())){
            //审核不通过
           throw new RuntimeException("此商品未通过审核");
        }
        Spu spu = new Spu();
        spu.setIsMarketable("1");
        spu.setId(id);
        spuMapper.updateByPrimaryKeySelective(spu);
    }

    /*
    批量上架
     通过：spuMapper.updateByExampleSelective(spu, example);方法  封装多个操作条件
    */
    @Override
    public int putMany(String[] ids) {
        Spu spu = new Spu();
        spu.setIsMarketable("1");   //上架商品
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();

        criteria.andIn("id", Arrays.asList(ids));  //设置所有上架商品的id

        criteria.andEqualTo("status","1"); // 通过审核的商品
        criteria.andEqualTo("isMarketable","0"); //下架的商品
        int count = spuMapper.updateByExampleSelective(spu, example);  //返回操作成功的总记录数
        return count;
    }

    /*
    批量下架：思路类似于批量上架
     */
    @Override
    public int pullMany(String[] ids) {
        Spu spu = new Spu();
        spu.setIsMarketable("0");   //下架商品
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();

        criteria.andIn("id", Arrays.asList(ids));  //设置所有上架商品的id

        criteria.andEqualTo("isMarketable","1"); //上架的商品
        int count = spuMapper.updateByExampleSelective(spu, example);  //返回操作成功的总记录数
        return count;
    }


    /**
     * 构建查询条件
     * @param searchMap
     * @return
     */
    private Example createExample(Map<String, Object> searchMap){
        Example example=new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        if(searchMap!=null){
            // 主键
            if(searchMap.get("id")!=null && !"".equals(searchMap.get("id"))){
                criteria.andLike("id","%"+searchMap.get("id")+"%");
            }
            // 货号
            if(searchMap.get("sn")!=null && !"".equals(searchMap.get("sn"))){
                criteria.andLike("sn","%"+searchMap.get("sn")+"%");
            }
            // SPU名
            if(searchMap.get("name")!=null && !"".equals(searchMap.get("name"))){
                criteria.andLike("name","%"+searchMap.get("name")+"%");
            }
            // 副标题
            if(searchMap.get("caption")!=null && !"".equals(searchMap.get("caption"))){
                criteria.andLike("caption","%"+searchMap.get("caption")+"%");
            }
            // 图片
            if(searchMap.get("image")!=null && !"".equals(searchMap.get("image"))){
                criteria.andLike("image","%"+searchMap.get("image")+"%");
            }
            // 图片列表
            if(searchMap.get("images")!=null && !"".equals(searchMap.get("images"))){
                criteria.andLike("images","%"+searchMap.get("images")+"%");
            }
            // 售后服务
            if(searchMap.get("saleService")!=null && !"".equals(searchMap.get("saleService"))){
                criteria.andLike("saleService","%"+searchMap.get("saleService")+"%");
            }
            // 介绍
            if(searchMap.get("introduction")!=null && !"".equals(searchMap.get("introduction"))){
                criteria.andLike("introduction","%"+searchMap.get("introduction")+"%");
            }
            // 规格列表
            if(searchMap.get("specItems")!=null && !"".equals(searchMap.get("specItems"))){
                criteria.andLike("specItems","%"+searchMap.get("specItems")+"%");
            }
            // 参数列表
            if(searchMap.get("paraItems")!=null && !"".equals(searchMap.get("paraItems"))){
                criteria.andLike("paraItems","%"+searchMap.get("paraItems")+"%");
            }
            // 是否上架
            if(searchMap.get("isMarketable")!=null && !"".equals(searchMap.get("isMarketable"))){
                criteria.andLike("isMarketable","%"+searchMap.get("isMarketable")+"%");
            }
            // 是否启用规格
            if(searchMap.get("isEnableSpec")!=null && !"".equals(searchMap.get("isEnableSpec"))){
                criteria.andLike("isEnableSpec","%"+searchMap.get("isEnableSpec")+"%");
            }
            // 是否删除
            if(searchMap.get("isDelete")!=null && !"".equals(searchMap.get("isDelete"))){
                criteria.andLike("isDelete","%"+searchMap.get("isDelete")+"%");
            }
            // 审核状态
            if(searchMap.get("status")!=null && !"".equals(searchMap.get("status"))){
                criteria.andLike("status","%"+searchMap.get("status")+"%");
            }

            // 品牌ID
            if(searchMap.get("brandId")!=null ){
                criteria.andEqualTo("brandId",searchMap.get("brandId"));
            }
            // 一级分类
            if(searchMap.get("category1Id")!=null ){
                criteria.andEqualTo("category1Id",searchMap.get("category1Id"));
            }
            // 二级分类
            if(searchMap.get("category2Id")!=null ){
                criteria.andEqualTo("category2Id",searchMap.get("category2Id"));
            }
            // 三级分类
            if(searchMap.get("category3Id")!=null ){
                criteria.andEqualTo("category3Id",searchMap.get("category3Id"));
            }
            // 模板ID
            if(searchMap.get("templateId")!=null ){
                criteria.andEqualTo("templateId",searchMap.get("templateId"));
            }
            // 运费模板id
            if(searchMap.get("freightId")!=null ){
                criteria.andEqualTo("freightId",searchMap.get("freightId"));
            }
            // 销量
            if(searchMap.get("saleNum")!=null ){
                criteria.andEqualTo("saleNum",searchMap.get("saleNum"));
            }
            // 评论数
            if(searchMap.get("commentNum")!=null ){
                criteria.andEqualTo("commentNum",searchMap.get("commentNum"));
            }

        }
        return example;
    }

}
