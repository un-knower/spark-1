package com.wzq.spark.utils

import java.util.Properties

import org.apache.hadoop.io.{LongWritable, Text}
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat
import org.apache.spark.sql.{Dataset, SparkSession}
import org.apache.spark.{SparkConf, SparkContext}

import scala.util.matching.Regex

case class Accounts_toDestiny(
                     date:String,
                     time:Int,
                     category:Int,
                     name:String,
                     channel:Int,
                     userLevel:Int,
                     userId:Int,
                     give:Int,
                     tools:Int,
                     singerId:Int,
                     singerLevel:Int,
                     contract:Int,
                     consumeGold:Int,
                     singer_gainGold:Int,
                     channel_gainGold:Int,
                     singer_currGold:Int,
                     channel_currGold:Int,
                     user_currGold:Int
                   )

object Accounts_toDestiny {

  val conf = new SparkConf().setAppName("mlib").setMaster("local[2]")
  val sc = new SparkContext(conf)
  val ss = SparkSession.builder().getOrCreate()
  def hdfsDataSet(file:String,filter:String,r:Regex): Dataset[Accounts_toDestiny] = {
    val rdd = sc.newAPIHadoopFile(file, classOf[TextInputFormat], classOf[LongWritable], classOf[Text]).map(pair => new String(pair._2.getBytes, 0, pair._2.getLength, "GBK"))
    val etl = rdd.filter(_.contains(filter))
    val transform = etl.map(m => {
      m match {
        case r(date, time, category, name, channel, userLevel, userId, give, tools, singerId, singerLevel, contract, consumeGold, singer_gainGold, channel_gainGold, singer_currGold, channel_currGold, user_currGold) =>
          Accounts_toDestiny(date, time.toInt, category.toInt, name, channel.toInt, userLevel.toInt, userId.toInt, give.toInt, tools.toInt, singerId.toInt, singerLevel.toInt, contract.toInt, consumeGold.toInt, singer_gainGold.toInt, channel_gainGold.toInt, singer_currGold.toInt, channel_currGold.toInt, user_currGold.toInt)
      }
    })
    import ss.implicits._
    val dataset = transform.toDF().as[Accounts_toDestiny]
    dataset
  }
  def fileDataset(file:String,filter:String,r:Regex): Dataset[Accounts_toDestiny]={
    val rdd=sc.textFile(file)
    val etl = rdd.filter(_.contains(filter))
    val transform = etl.map(m => {
      m match {
        case r(date, time, category, name, channel, userLevel, userId, give, tools, singerId, singerLevel, contract, consumeGold, singer_gainGold, channel_gainGold, singer_currGold, channel_currGold, user_currGold) =>
          Accounts_toDestiny(date, time.toInt, category.toInt, name, channel.toInt, userLevel.toInt, userId.toInt, give.toInt, tools.toInt, singerId.toInt, singerLevel.toInt, contract.toInt, consumeGold.toInt, singer_gainGold.toInt, channel_gainGold.toInt, singer_currGold.toInt, channel_currGold.toInt, user_currGold.toInt)
      }
    })
    import ss.implicits._
    val dataset = transform.toDF().as[Accounts_toDestiny]
    dataset
  }
  def toMYSQL(file:String,filter:String,r:Regex,mode:String,url:String,tablename:String,properties:Properties)={
    hdfsDataSet(file,filter,r).write.mode(mode).jdbc(url,tablename,properties)
  }
  def readMYSQL(url:String,tablename:String,properties:Properties)={
    ss.read.jdbc(url,tablename,properties)
  }
  def toHiveTmp(file:String,filter:String,r:Regex,tmpView:String)={
    hdfsDataSet(file,filter,r).createOrReplaceTempView(tmpView)
  }
  def toHIve(file:String,filter:String,r:Regex,mode:String,tabName:String)={
    hdfsDataSet(file,filter,r).write.mode(mode).saveAsTable(tabName)
  }

  def main(args: Array[String]): Unit = {
    val file="data/accounts.log"
    val filter="金币消费结算统计"
    val r ="""^(\S+) Bill\[\d+\]  INFO: \[金币消费结算统计\]时间\((\d+)\)类别\((\d+)\)名称\((.*?)\)频道\((\d+)\)等级\((\d+)\)用户\((\d+)\)赠送\((\d+)\)个道具\((\d+)\)给歌手\((\d+)\),歌手等级\((\d+)\),签约\((\d+)\), 消耗金币\((\d+)\), 歌手获得金币\((\d+)\), 频道获得金币\((\d+)\),歌手当前金币\((\d+)\)频道当前金币\((\d+)\)用户当前金币\((\d+)\)""".r
    val mode="append"
    val tmpView="accounts"
    val url="jdbc:mysql://192.168.94.7:3306/mystudy"
    val tablename="accounts_toMSQL"
    val tabName="accounts_toHive"
    val properties=new Properties()
    properties.put("user","root")
    properties.put("password","zhiqun")
    toMYSQL(file,filter,r,mode,url,tablename,properties)
    toHIve(file,filter,r,mode,tabName)
  }
}
