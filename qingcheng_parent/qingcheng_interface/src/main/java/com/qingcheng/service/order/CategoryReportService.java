package com.qingcheng.service.order;

import com.qingcheng.pojo.order.CategoryReport;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * @Author:JiashengPang
 * @Description:
 * @Date: 9:44 2019/12/13
 */
public interface CategoryReportService {

    /*
    统计各类订单在指定时间的金额
     */
    public List<CategoryReport> categoryReport( LocalDate date);

    public List<Map>  category1Count( String date1,  String date2);

    public void createData();
}
