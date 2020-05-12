package com.itzx.gmall.publisher.service.impl;

import com.itzx.gmall.publisher.mapper.DauMapper;
import com.itzx.gmall.publisher.service.PublisherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author: yyeleven
 * create: 2020/5/10 23:59
 */
@Service
public class PublisherServiceImpl implements PublisherService {

    @Autowired
    DauMapper dauMapper;

    @Override
    public Long getDauTotal(String date) {
        return dauMapper.selectDauTotal(date);
    }

    @Override
    public Map<String, Long> getDauTotalHours(String date) {
        // 变换格式
        List<Map> dauListMap = dauMapper.selectDauTotalHours(date);
        Map dauMap = new HashMap();
        for (Map map : dauListMap) {
            String lh = (String) map.get("LH");
            Long ct = (Long) map.get("CT");
            dauMap.put(lh, ct);
        }
        return dauMap;
    }
}
