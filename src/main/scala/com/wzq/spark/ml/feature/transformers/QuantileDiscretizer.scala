package com.wzq.spark.ml.feature.transformers

import org.apache.spark.ml.feature.QuantileDiscretizer
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SparkSession

/**
  * 将连续数值特征转换为离散类别特征
  * 调用函数计算分位数，并完成离散化
  *
  */
object QuantileDiscretizer {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("mlib").setMaster("local[2]")
    val sc = new SparkContext(conf)
    val ss = SparkSession.builder().getOrCreate()

    val data = Array((0, 18.0), (1, 19.0), (2, 8.0), (3, 5.0), (4, 2.2))
    val df = ss.createDataFrame(data).toDF("id", "hour")

    val discretizer = new QuantileDiscretizer()
      .setInputCol("hour")
      .setOutputCol("result")
      .setNumBuckets(3)  //设置分类数目
    discretizer.fit(df).transform(df).show(false)

    /**
    +---+----+------+
|id |hour|result|
+---+----+------+
|0  |18.0|2.0   |
|1  |19.0|2.0   |
|2  |8.0 |1.0   |
|3  |5.0 |1.0   |
|4  |2.2 |0.0   |
+---+----+------+
      */
  }
}
