package com.wzq.elastic

import org.apache.spark.sql.SparkSession
import org.apache.spark.{SparkConf, SparkContext}
import org.elasticsearch.spark._

object ReadES {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("es").setMaster("local[2]")
      .set("es.nodes", "211.159.201.95")
      .set("es.port", "19200")
      .set("path", "baseinfo-2017.09.07/baseinfo")
    val sc = new SparkContext(conf)

    val esrdd=sc.esRDD("baseinfo-2017.09.07/baseinfo","?q=*")
    println(esrdd.count())

    val ss=SparkSession.builder().getOrCreate()

    /*val df = ss.read
      .format("org.elasticsearch.spark.sql")
      .option("es.nodes", "211.159.201.95")
      .option("es.port", "19200")
      .option("path", "baseinfo-2017.09.07/baseinfo")
      .load()
    df.show(10)*/

  }
}
