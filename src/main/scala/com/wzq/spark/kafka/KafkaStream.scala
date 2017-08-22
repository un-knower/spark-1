package com.wzq.spark.kafka

import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.{Seconds, State, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.KafkaUtils

/**
  * create a case class map the table fields
  * @param age
  * @param name
  */
case class KafkaStream(age:Int,name:String)
object KafkaStream {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("stream").setMaster("local[2]")
    val sc=new SparkContext(conf)
    val ssc = new StreamingContext(sc, Seconds(2))
    val ss = SparkSession.builder().config(conf).enableHiveSupport().getOrCreate()
    val kafkaParams=Map(
      "bootstrap.servers"->"192.168.94.6:9092",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "filebeat",
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (false: java.lang.Boolean))
    val topics = Array("filebeat")
    val  kafkaStream = KafkaUtils.createDirectStream[String, String](ssc, PreferConsistent, Subscribe[String,String](topics,kafkaParams))
    val messages=kafkaStream.map(_.value()).transform(rdd=>{
      val parse=ss.read.json(rdd)
      parse.rdd
    })
    import ss.implicits._
    messages.foreachRDD(rdd=>{
      val df=rdd.map(a=>KafkaStream(a(0).toString.toInt,a(1).toString)).toDF()
      df.write.mode("append").saveAsTable("filebeat")
    })
    ssc.start()
    ssc.awaitTermination()
  }
}
