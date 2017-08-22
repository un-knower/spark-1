package com.wzq.spark.ml.feature.transformers

import org.apache.spark.ml.feature.Binarizer
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SparkSession

/**
  * 标签二值化
  * 特征值高于阈值为1，小于等于为0
  */
object Binarizer {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("mlib").setMaster("local[2]")
    val sc = new SparkContext(conf)
    val ss = SparkSession.builder().getOrCreate()
    val data = Array((0, 0.1), (1, 0.8), (2, 0.2))
    val dataFrame = ss.createDataFrame(data).toDF("id", "feature")
    val binarizer = new Binarizer()
      .setInputCol("feature")
      .setOutputCol("binarized_feature")
      .setThreshold(0.5)
    val binarizedDataFrame = binarizer.transform(dataFrame)
    binarizedDataFrame.show(false)

    /**
    +---+-------+-----------------+
|id |feature|binarized_feature|
+---+-------+-----------------+
|0  |0.1    |0.0              |
|1  |0.8    |1.0              |
|2  |0.2    |0.0              |
+---+-------+-----------------+
      */
  }
}
