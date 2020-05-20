package com.itzx.gmall.publisher.controller;

import com.alibaba.fastjson.JSON;
import com.itzx.gmall.publisher.service.PublisherService;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * author: yyeleven
 * create: 2020/5/11 00:01
 */
@RestController
public class PublisherController {

    @Autowired
    PublisherService publisherService;

    @GetMapping("realtime-total")
    public String getRealtimeTotal(@RequestParam("date") String dateString) {
        Long dauTotal = publisherService.getDauTotal(dateString);

        List<Map> totalList = new ArrayList<>();
        Map<Object, Object> dauMap = new HashMap<>();
        dauMap.put("id", "dau");
        dauMap.put("name", "新增日活");
        dauMap.put("value", dauTotal);

        totalList.add(dauMap);

        Map<Object, Object> midMap = new HashMap<>();
        midMap.put("id", "new_mid");
        midMap.put("name", "新增设备");
        midMap.put("value", 323);

        totalList.add(midMap);

        // 总交易额
        final Double orderAmount = publisherService.getOrderAmount(dateString);
        final HashMap<Object, Object> orderAmountMap = new HashMap<>();
        orderAmountMap.put("id", "order_amount");
        orderAmountMap.put("name", "新增交易额");
        orderAmountMap.put("value", orderAmount);

        totalList.add(orderAmountMap);

        return JSON.toJSONString(totalList);
    }

    @GetMapping("realtime-hour")
    public String getRealtimeHour(@RequestParam("id") String id, @RequestParam("date") String dateString) {
        if ("dau".equals(id)) {
            final Map<String, Long> dauTotalHoursTD = publisherService.getDauTotalHours(dateString);
            final String yesterday = getYesterday(dateString);
            final Map<String, Long> dauTotalHoursYD = publisherService.getDauTotalHours(yesterday);

            Map hourMap = new HashMap();
            hourMap.put("today", dauTotalHoursTD);
            hourMap.put("yesterday", dauTotalHoursYD);

            return JSON.toJSONString(hourMap);
        } else if ("order_amount".equals(id)) {
            final Map<String, Double> orderAmountHoursTD = publisherService.getOrderAmountHours(dateString);
            final String yesterday = getYesterday(dateString);
            final Map<String, Double> orderAmountHoursYD = publisherService.getOrderAmountHours(yesterday);

            final HashMap<Object, Object> hourMap = new HashMap<>();
            hourMap.put("today", orderAmountHoursTD);
            hourMap.put("yesterday", orderAmountHoursYD);

            return JSON.toJSONString(hourMap);
        } else {
            return null;
        }
    }

    private String getYesterday(String today) {
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            final Date todayD = sdf.parse(today);
            final Date yesterdayD = DateUtils.addDays(todayD, -1);
            final String yesterday = sdf.format(yesterdayD);
            return yesterday;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

}
