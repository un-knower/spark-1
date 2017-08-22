package com.wzq.spark.streaming

import java.util.Properties
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.{Seconds, StreamingContext}

case class StreamTomysql(id:Int,name:String)
object StreamTomysql {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("mysql").setMaster("local[2]")
    val sc=new SparkContext(conf)
    val ss = SparkSession.builder().config(conf).getOrCreate()
    val ssc = new StreamingContext(sc, Seconds(2))
    val messages=ssc.socketTextStream("192.168.94.7",9999)
    import ss.implicits._
    messages.foreachRDD(rdd=>{
      val df=rdd.map(_.split(",")).map(s=>StreamTomysql(s(0).toInt,s(1))).toDF()
      val url="jdbc:mysql://192.168.94.6:3306/mystudy"
      val tablename="toMSQL"
      val properties=new Properties()
      properties.put("user","root")
      properties.put("password","zhiqun")
      df.write.mode("append").jdbc(url,tablename,properties)
    })
    ssc.start()
    ssc.awaitTermination()
  }
}
