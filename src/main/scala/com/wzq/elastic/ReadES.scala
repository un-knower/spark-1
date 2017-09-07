package com.wzq.elastic

import org.apache.spark.sql.SparkSession
import org.apache.spark.{SparkConf, SparkContext}

object ReadES {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("es").setMaster("local[2]")
    val sc = new SparkContext(conf)
    val ss=SparkSession.builder().getOrCreate()
/*    val df = ss.read
      .format("org.elasticsearch.spark.sql")
      .option("es.nodes", "125.39.176.80")
      .option("es.port", "9200")
      .option("path", "data/shakespeare.json")
      .load()
    df.show()*/
  }
}
