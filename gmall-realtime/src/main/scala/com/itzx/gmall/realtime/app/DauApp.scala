package com.itzx.gmall.realtime.app

import java.text.SimpleDateFormat
import java.util
import java.util.Date

import com.alibaba.fastjson.JSON
import com.itzx.gmall.common.constants.GmallConstant
import com.itzx.gmall.realtime.model.StartUpLog
import com.itzx.gmall.realtime.util.{MyKafkaUtil, RedisUtil}
import org.apache.hadoop.conf.Configuration
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.spark.SparkConf
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.dstream.{DStream, InputDStream}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.phoenix.spark._
import redis.clients.jedis.Jedis

/**
 *
 *
 * author: yyeleven
 * create: 2020/4/23 00:24
 */
object DauApp {

  def main(args: Array[String]): Unit = {

    val sparkConf = new SparkConf().setAppName("dau_app").setMaster("local[*]")

    val ssc = new StreamingContext(sparkConf, Seconds(5))

    val inputDstream: InputDStream[ConsumerRecord[String, String]] =
      MyKafkaUtil.getKafkaStream(GmallConstant.KAFKA_STARTUP, ssc)

    // 转换格式 同时补充两个时间字段
    val startUplogDstream: DStream[StartUpLog] = inputDstream.map(record => {
      val jsonString: String = record.value()
      val startUpLog: StartUpLog = JSON.parseObject(jsonString, classOf[StartUpLog])
      val format: SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH")
      val dateHour: String = format.format(new Date(startUpLog.ts))
      val dateHourArr: Array[String] = dateHour.split(" ")
      startUpLog.logDate = dateHourArr(0)
      startUpLog.logHour = dateHourArr(1)
      startUpLog
    }).cache()

    // 根据清单进行过滤
    startUplogDstream.transform(rdd => {  // 每一个批次执行一次
      rdd.saveToPhoenix("GMALL_DAU",
        Seq( "MID", "UID", "APPID", "AREA", "OS", "CH", "TYPE", "VS", "LOGDATE", "LOGHOUR", "TS"),
        new Configuration,
        Some("node-1,node-2,node-3:2181"))

      // driver 每一个批次执行一次
      println("过滤前:" + rdd.count())
      val jedis = RedisUtil.getJedisClient
      val dataString: String = new SimpleDateFormat("yyyy-MM-dd").format(new Date())
      val dauKey = "dau:" + dataString
      val dauMidSet: util.Set[String] = jedis.smembers(dauKey)
      jedis.close()
      val dauMidBC: Broadcast[util.Set[String]] = ssc.sparkContext.broadcast(dauMidSet)

      val filteredRdd: RDD[StartUpLog] = rdd.filter(startuplog => {
        val dauMidSet: util.Set[String] = dauMidBC.value
        val flag: Boolean = dauMidSet.contains(startuplog.mid)
        !flag
      })
      println("过滤后:" + filteredRdd.count())
      filteredRdd
    }).map(startuplog => (startuplog.mid, startuplog)) // 批次内去重, 同一批次内, 相同mid只保留第一条 => 对相同的mid进行分组,组内进行比较, 保留第一条
      .groupByKey()
      .flatMap{case (mid, startupItr) =>

        startupItr.toList.sortWith{(startup1, startup2) =>
          startup1.ts < startup2.ts
        }.take(1)
      }.foreachRDD(rdd => { // 把用户访问清单保存到redis中

        rdd.foreachPartition(partition => {
          val jedis: Jedis = RedisUtil.getJedisClient
          partition.foreach(startuplog => {
            val dauKey = "dau:" + startuplog.logDate
            jedis.sadd(dauKey, startuplog.mid)
          })
          jedis.close()
        })
      })

    ssc.start()
    ssc.awaitTermination()
  }
}
