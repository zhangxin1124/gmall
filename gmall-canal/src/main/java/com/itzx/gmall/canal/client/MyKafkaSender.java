package com.itzx.gmall.canal.client;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

/**
 * kafka发送数据的工具类
 *
 * author: yyeleven
 * create: 2020/5/13 10:11
 */
public class MyKafkaSender {

    public static KafkaProducer<String, String> kafkaProducer = null;

    public static KafkaProducer<String, String> createKafkaProducer() {

        Properties properties = new Properties();
        properties.put("bootstrap.servers", "node-1:9092,node-2:9092,node-3:9092");
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        KafkaProducer<String, String> producer = null;

        try {
            producer = new KafkaProducer<String, String>(properties);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return producer;
    }

    public static void send(String topic, String msg) {
        if (kafkaProducer == null) {
            kafkaProducer = createKafkaProducer();
        }
        kafkaProducer.send(new ProducerRecord<String, String>(topic, msg));
    }

}
