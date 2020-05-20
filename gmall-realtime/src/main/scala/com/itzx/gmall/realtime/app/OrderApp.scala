package com.itzx.gmall.realtime.app

import com.alibaba.fastjson.JSON
import com.itzx.gmall.common.constants.GmallConstant
import com.itzx.gmall.realtime.model.OrderInfo
import com.itzx.gmall.realtime.util.MyKafkaUtil
import org.apache.hadoop.conf.Configuration
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.{DStream, InputDStream}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.phoenix.spark._


/**
 *
 *
 * author: yyeleven
 * create: 2020/5/13 23:03
 */
object OrderApp {

  def main(args: Array[String]): Unit = {

    val conf: SparkConf = new SparkConf()
      .setMaster("local[*]")
      .setAppName("order_app")
    val ssc: StreamingContext = new StreamingContext(conf, Seconds(5))

    val inputDstream: InputDStream[ConsumerRecord[String, String]] =
      MyKafkaUtil.getKafkaStream(GmallConstant.KAFKA_TOPIC_ORDER, ssc)

    // 变换结构 record  =>  case Class
    val orderDstream: DStream[OrderInfo] = inputDstream.map(record => {
      val jsonString: String = record.value()
      val orderInfo: OrderInfo = JSON.parseObject(jsonString, classOf[OrderInfo])
      val createTimeArr: Array[String] = orderInfo.create_time.split(" ")

      orderInfo.create_date = createTimeArr(0)
      orderInfo.create_hour = createTimeArr(0).split(":")(1)
      val tel3_8: (String, String) = orderInfo.consignee_tel.splitAt(3)
      val front3: String = tel3_8._1
      val back4: String = tel3_8._2.splitAt(4)._2
      orderInfo.consignee_tel = front3 + "****" + back4
      orderInfo
    })

    orderDstream.foreachRDD(rdd => {

      val configuration = new Configuration()

      rdd.saveToPhoenix("GMALL_ORDER_INFO",
        Seq("ID","PROVINCE_ID",
          "CONSIGNEE", "ORDER_COMMENT", "CONSIGNEE_TEL", "ORDER_STATUS",
          "PAYMENT_WAY", "USER_ID","IMG_URL", "TOTAL_AMOUNT", "EXPIRE_TIME",
          "DELIVERY_ADDRESS",
          "CREATE_TIME","OPERATE_TIME","TRACKING_NO","PARENT_ORDER_ID","OUT_TRADE_NO",
          "TRADE_BODY", "CREATE_DATE", "CREATE_HOUR"),
        configuration,
        Some("node-1,node-2,node-3:2181")
      )
    })

    ssc.start()
    ssc.awaitTermination()
  }

}
