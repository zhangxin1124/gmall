package com.itzx.gmall.publisher.mapper;

import java.util.List;
import java.util.Map;

/**
 * author: yyeleven
 * create: 2020/5/13 23:49
 */
public interface OrderMapper {

    public Double selectOrderAmount(String date);

    public List<Map> selectOrderAmountHour(String date);
}
