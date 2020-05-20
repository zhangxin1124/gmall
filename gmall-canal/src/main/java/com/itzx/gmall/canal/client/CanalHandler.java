package com.itzx.gmall.canal.client;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.itzx.gmall.common.constants.GmallConstant;
import javafx.beans.property.ReadOnlyBooleanWrapper;

import java.util.List;

/**
 * author: yyeleven
 * create: 2020/5/13 09:27
 */
public class CanalHandler {

    CanalEntry.EventType eventType;

    String tableName;

    List<CanalEntry.RowData> rowDataList;

    public CanalHandler(CanalEntry.EventType eventType, String tableName, List<CanalEntry.RowData> rowDataList) {
        this.eventType = eventType;
        this.tableName = tableName;
        this.rowDataList = rowDataList;
    }

    public void handle() {
        // 下单操作
        if ("order_info".equals(tableName) && CanalEntry.EventType.INSERT == eventType) {
            rowDateList2Kafka(GmallConstant.KAFKA_TOPIC_ORDER);
        } else if ("user_info".equals(tableName) && (CanalEntry.EventType.INSERT == eventType || CanalEntry.EventType.UPDATE == eventType)) {
            rowDateList2Kafka(GmallConstant.KAFKA_TOPIC_USER);
        }
    }

    public void rowDateList2Kafka(String kafkaTopic) {
        for (CanalEntry.RowData rowData : rowDataList) {
            final List<CanalEntry.Column> columnsList = rowData.getAfterColumnsList();
            final JSONObject jsonObject = new JSONObject();
            for (CanalEntry.Column column : columnsList) {
                System.out.println(column.getName() + "::::" + column.getValue());
                jsonObject.put(column.getName(), column.getValue());
            }
            MyKafkaSender.send(kafkaTopic, jsonObject.toJSONString());
        }
    }

}
