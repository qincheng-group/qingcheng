package com.qingcheng.controller.order;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.pojo.order.CategoryReport;
import com.qingcheng.service.order.CategoryReportService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/categoryReport")
public class CategoryReportController {

    @Reference
    private CategoryReportService categoryReportService;

    /**
     * 昨天的数据统计（商品类目）
     * @return
     */
    @GetMapping("/yesterday")
    public List<CategoryReport> yesterday(){
        LocalDate localDate= LocalDate.now().minusDays(1);//得到昨天的日期
        return categoryReportService.categoryReport(localDate);
    }


    /*
    统计一级分类的商品类目
     */
    @GetMapping("/category1Count")
    public List<Map> category1Count (String date1,String date2){
        return categoryReportService.category1Count("2019-04-15","2019-04-16");
    }

}
