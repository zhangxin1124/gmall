package com.itzx.gmall.publisher.service;

import java.util.Map;

/**
 * author: yyeleven
 * create: 2020/5/10 23:57
 */
public interface PublisherService {

    public Long getDauTotal(String date);

    public Map<String, Long> getDauTotalHours(String date);
}
