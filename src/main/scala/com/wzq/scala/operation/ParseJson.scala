package com.wzq.scala.operation

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SparkSession

/**
  * json日志处理
  */
object ParseJson {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("json").setMaster("local[2]")
    val sc=new SparkContext(conf)
    val ss = SparkSession.builder().getOrCreate()
    val jrdd=sc.textFile("data/room-20170829.log")
    val filter=jrdd.filter(_.contains("path"))
    val df=ss.read.json(filter)
    df.createOrReplaceTempView("tmp")
    ss.sql("select * from tmp").show(50)
  }
}
