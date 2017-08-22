package com.wzq.spark.ml.feature.transformers

import org.apache.spark.ml.feature.MaxAbsScaler
import org.apache.spark.ml.linalg.Vectors
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SparkSession

/**
  * 特征值在[-1,1]之间
  */
object MaxAbsScaler {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("mlib").setMaster("local[2]")
    val sc = new SparkContext(conf)
    val ss = SparkSession.builder().getOrCreate()
    val dataFrame = ss.createDataFrame(Seq(
      (0, Vectors.dense(1.0, 0.1,8.0)),
      (1, Vectors.dense(2.0, 1.0, -4.0)),
      (2, Vectors.dense(4.0, 10.0, 8.0))
    )).toDF("id", "features")

    val scaler = new MaxAbsScaler()
      .setInputCol("features")
      .setOutputCol("scaledFeatures")
    val scalerModel = scaler.fit(dataFrame)
    scalerModel.transform(dataFrame).show(false)

    /**
    +---+--------------+----------------+
|id |features      |scaledFeatures  |
+---+--------------+----------------+
|0  |[1.0,0.1,-8.0]|[0.25,0.01,-1.0]|
|1  |[2.0,1.0,-4.0]|[0.5,0.1,-0.5]  |
|2  |[4.0,10.0,8.0]|[1.0,1.0,1.0]   |
+---+--------------+----------------+
      */
  }
}
