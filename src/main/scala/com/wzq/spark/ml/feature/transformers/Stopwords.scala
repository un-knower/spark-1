package com.wzq.spark.ml.feature.transformers

import org.apache.spark.ml.feature.StopWordsRemover
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SparkSession

/**
  * 在各种需要处理文本的地方，对这些停止词做出一些特殊处理，以方便更关注在更重要的一些词上
  * 一般有三类：限定词、并列连词、介词
  *
  */
object Stopwords {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("mlib").setMaster("local[2]")
    val sc = new SparkContext(conf)
    val ss = SparkSession.builder().getOrCreate()
    val dataSet = ss.createDataFrame(Seq(
      (0, Seq("I", "saw", "the", "red", "balloon")),
      (1, Seq("Mary", "had", "a", "little", "lamb"))
    )).toDF("id", "raw")
    val remover = new StopWordsRemover()
      .setInputCol("raw")
      .setOutputCol("filtered")
    remover.transform(dataSet).show(false)

    /**
    +---+----------------------------+--------------------+
|id |raw                         |filtered            |
+---+----------------------------+--------------------+
|0  |[I, saw, the, red, balloon] |[saw, red, balloon] |
|1  |[Mary, had, a, little, lamb]|[Mary, little, lamb]|
+---+----------------------------+--------------------+
      */
  }
}
