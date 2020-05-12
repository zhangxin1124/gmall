package com.itzx.gmall.realtime.model

/**
 *
 *
 * author: yyeleven
 * create: 2020/4/23 00:55
 */
case class StartUpLog(
                       mid: String,
                       uid: String,
                       appid: String,
                       area: String,
                       os: String,
                       ch: String,
                       logType: String,
                       vs: String,
                       var logDate: String,
                       var logHour: String,
                       ts: Long
                     )