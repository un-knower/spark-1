package com.wzq.spark.ml.feature.transformers

import org.apache.spark.ml.feature.Bucketizer
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SparkSession

/**
  * 将连续数值转换为离散类别,分桶
  * 分类标准splits自定义，但要递增顺序
  */
object Bucketizer {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("mlib").setMaster("local[2]")
    val sc = new SparkContext(conf)
    val ss = SparkSession.builder().getOrCreate()
    val splits = Array(Double.NegativeInfinity, -0.5, 0.0, 0.5, Double.PositiveInfinity)
    val data = Array(-999.9, -0.5, -0.3, 0.0, 0.2, 999.9)
    val dataFrame = ss.createDataFrame(data.map(Tuple1.apply)).toDF("features")
    val bucketizer = new Bucketizer()
      .setInputCol("features")
      .setOutputCol("bucketedFeatures")
      .setSplits(splits)
    bucketizer.transform(dataFrame).show(false)

    /**
    +--------+----------------+
|features|bucketedFeatures|
+--------+----------------+
|-999.9  |0.0             |
|-0.5    |1.0             |
|-0.3    |1.0             |
|0.0     |2.0             |
|0.2     |2.0             |
|999.9   |3.0             |
+--------+----------------+
      */
  }
}
