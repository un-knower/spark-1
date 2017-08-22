package com.wzq.spark.core

import org.apache.spark.{SparkConf, SparkContext}

object Wordcount {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("wordcount").setMaster("local[2]")
    val sc=new SparkContext(conf)
    val rdd=sc.textFile("data/word")
    val wc=rdd.flatMap(line=>line.split(",")).map((_,1)).reduceByKey(_+_)
    wc.collect().foreach(println(_))
  }
}
