package com.itzx.gmall.canal.client;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import com.google.protobuf.InvalidProtocolBufferException;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * author: yyeleven
 * create: 2020/5/13 09:15
 */
public class CanalClient {

    public void watch(String hostname,int port,String destination ,String tables) {
        // 连接
        CanalConnector canalConnector = 
                CanalConnectors.newSingleConnector(new InetSocketAddress(hostname, port), destination, "", "");
        while (true) {
            canalConnector.connect();
            canalConnector.subscribe(tables);
            final Message message = canalConnector.get(100);
            final int size = message.getEntries().size();
            if (size == 0) {
                System.out.println("没有数据！！息休息5秒");

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                for (CanalEntry.Entry entry : message.getEntries()) {
                    if (entry.getEntryType() == CanalEntry.EntryType.ROWDATA) {
                        CanalEntry.RowChange rowChange = null;
                        try {
                            rowChange = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
                        } catch (InvalidProtocolBufferException e) {
                            e.printStackTrace();
                        }
                        final String tableName = entry.getHeader().getTableName();  // 表名
                        final CanalEntry.EventType eventType = rowChange.getEventType();  // 修改类型
                        final List<CanalEntry.RowData> rowDatasList = rowChange.getRowDatasList();  // 行集  数据

                        final CanalHandler canalHandler = new CanalHandler(eventType, tableName, rowDatasList);
                        canalHandler.handle();
                    }
                }
            }

        }
        
        
        
    }
}
