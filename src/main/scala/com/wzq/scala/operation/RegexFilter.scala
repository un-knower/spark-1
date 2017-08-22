package com.wzq.scala.operation

import org.apache.hadoop.io.{LongWritable, Text}
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SparkSession

case class RegexFilter(
                     date:String,
                     time:Int,
                     category:Int,
                     name:String,
                     channel:Int,
                     userLevel:Int,
                     userId:Int,
                     give:Int,
                     tools:Int,
                     singer:Int,
                     singerLevel:Int,
                     contract:Int,
                     consumeGold:Int,
                     singer_gainGold:Int,
                     channel_gainGold:Int,
                     singer_currGold:Int,
                     channel_currGold:Int,
                     user_currGold:Int
                   )
object RegexFilter {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("etl").setMaster("local[2]")
    val sc=new SparkContext(conf)
    val ss = SparkSession.builder().enableHiveSupport().getOrCreate()
    val path="data/accounts.log"
    //防止乱码
    val rdd=sc.newAPIHadoopFile(path,classOf[TextInputFormat],classOf[LongWritable],classOf[Text]).map(pair => new String(pair._2.getBytes, 0, pair._2.getLength, "GBK"))
    val etl=rdd.filter(_.contains("金币消费结算统计"))
    val transform=etl.map(m=>{
      val r="""^(\S+) Bill\[\d+\]  INFO: \[金币消费结算统计\]时间\((\d+)\)类别\((\d+)\)名称\((.*?)\)频道\((\d+)\)等级\((\d+)\)用户\((\d+)\)赠送\((\d+)\)个道具\((\d+)\)给歌手\((\d+)\),歌手等级\((\d+)\),签约\((\d+)\), 消耗金币\((\d+)\), 歌手获得金币\((\d+)\), 频道获得金币\((\d+)\),歌手当前金币\((\d+)\)频道当前金币\((\d+)\)用户当前金币\((\d+)\)""".r
      m match {
        case r(date,time,category,name,channel,userLevel,userId,give,tools,singer,singerLevel,contract,consumeGold,singer_gainGold,channel_gainGold,singer_currGold,channel_currGold,user_currGold)=>
          RegexFilter(date,time.toInt,category.toInt,name,channel.toInt,userLevel.toInt,userId.toInt,give.toInt,tools.toInt,singer.toInt,singerLevel.toInt,contract.toInt,consumeGold.toInt,singer_gainGold.toInt,channel_gainGold.toInt,singer_currGold.toInt,channel_currGold.toInt,user_currGold.toInt)
      }
    })
    import ss.implicits._
    val df=transform.toDF().as[RegexFilter]
    df.createOrReplaceTempView("accounts")

    //使用spark sql建立分区表
    ss.sql("create table  if not exists gold_consume_accounts(time int,category int,name string,channel int,userLevel int,userId int,give int,tools int,singer int,singerLevel int,contract int,consumeGold int,singer_gainGold int,channel_gainGold int,singer_currGold int,channel_currGold int,user_currGold int) PARTITIONED BY(date string) STORED AS PARQUET location '/user/hive/warehouse'")
    ss.sql(s"insert overwrite table gold_consume_accounts PARTITION (date=2017) select time,category,name,channel,userLevel,userId,give,tools,singer,singerLevel,contract,consumeGold,singer_gainGold,channel_gainGold,singer_currGold,channel_currGold,user_currGold from accounts")
  }
}
