package com.qingcheng.pojo.goods;

import java.io.Serializable;
import java.util.List;

/**
 * @Author:JiashengPang
 * @Description:   封装sku和spu的组合实体类
 * @Date: 14:30 2019/12/11
 */
public class Goods implements Serializable {

    private Spu spu;

    private List<Sku> skuList;

    public Spu getSpu() {
        return spu;
    }

    public void setSpu(Spu spu) {
        this.spu = spu;
    }

    public List<Sku> getSkuList() {
        return skuList;
    }

    public void setSkuList(List<Sku> skuList) {
        this.skuList = skuList;
    }
}
