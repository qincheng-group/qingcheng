package com.qingcheng.controller.order;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.service.order.CategoryReportService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

/*
定时任务
 */
@Component
public class OrderTask {

    /*
    测试代码
     */
    @Scheduled(cron = "10,12,16-20 * * * * ?")
    public void orderTimeOutLogic(){
        System.out.println(new Date());
    }


    @Reference
    private CategoryReportService categoryReportService;



    /*
    定时任务：每天一点钟把商品类目信息同步到tb_category_report中
     */
    @Scheduled(cron = "0 * * * * ?")
    public void createCategoryReportDate(){
        System.out.println("生成类目统计数据");
        categoryReportService.createData();
    }


}
