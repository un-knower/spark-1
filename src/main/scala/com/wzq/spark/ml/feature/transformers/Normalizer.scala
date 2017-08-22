package com.wzq.spark.ml.feature.transformers

import org.apache.spark.ml.feature.Normalizer
import org.apache.spark.ml.linalg.Vectors
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SparkSession

/**
  * 向量的规范化
  * L1范数，L2范数
  */
object Normalizer {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("mlib").setMaster("local[2]")
    val sc = new SparkContext(conf)
    val ss = SparkSession.builder().getOrCreate()
    val dataFrame = ss.createDataFrame(Seq(
      (0, Vectors.dense(1.0, 0.5, -1.0)),
      (1, Vectors.dense(2.0, 1.0, 1.0)),
      (2, Vectors.dense(4.0, 10.0, 2.0))
    )).toDF("id", "features")
    val normalizer = new Normalizer()
      .setInputCol("features")
      .setOutputCol("normFeatures")
      .setP(1.0)  //p=2默认的
    val l1NormData = normalizer.transform(dataFrame)
    l1NormData.show(false)

    /**
    +---+--------------+------------------+
|id |features      |normFeatures      |
+---+--------------+------------------+
|0  |[1.0,0.5,-1.0]|[0.4,0.2,-0.4]    |
|1  |[2.0,1.0,1.0] |[0.5,0.25,0.25]   |
|2  |[4.0,10.0,2.0]|[0.25,0.625,0.125]|
+---+--------------+------------------+
      */
  }
}
