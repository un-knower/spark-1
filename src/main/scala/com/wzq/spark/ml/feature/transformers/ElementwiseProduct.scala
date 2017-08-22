package com.wzq.spark.ml.feature.transformers

import org.apache.spark.ml.feature.ElementwiseProduct
import org.apache.spark.ml.linalg.Vectors
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SparkSession

/**
  * 每个矩阵元素对应相乘
  */
object ElementwiseProduct {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("mlib").setMaster("local[2]")
    val sc = new SparkContext(conf)
    val ss = SparkSession.builder().getOrCreate()
    val dataFrame = ss.createDataFrame(Seq(
      ("a", Vectors.dense(1.0, 2.0, 3.0)),
      ("b", Vectors.dense(4.0, 5.0, 6.0)))).toDF("id", "vector")
    val transformingVector = Vectors.dense(0.0, 1.0, 2.0)
    val transformer = new ElementwiseProduct()
      .setScalingVec(transformingVector)
      .setInputCol("vector")
      .setOutputCol("transformedVector")
    transformer.transform(dataFrame).show(false)

    /**

    +---+-------------+-----------------+
|id |vector       |transformedVector|
+---+-------------+-----------------+
|a  |[1.0,2.0,3.0]|[0.0,2.0,6.0]    |
|b  |[4.0,5.0,6.0]|[0.0,5.0,12.0]   |
+---+-------------+-----------------+
      */
  }
}
