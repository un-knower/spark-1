package com.wzq.spark.ml.feature.transformers

import org.apache.spark.ml.feature.MinMaxScaler
import org.apache.spark.ml.linalg.Vectors
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SparkSession

/**
  * 将所有特征向量线性变换到用户指定最大最小值之间，一般为[0,1]
  */
object MinMaxScaler {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("mlib").setMaster("local[2]")
    val sc = new SparkContext(conf)
    val ss = SparkSession.builder().getOrCreate()
    val dataFrame = ss.createDataFrame(Seq(
      (0, Vectors.dense(1.0, 0.1, -1.0)),
      (1, Vectors.dense(2.0, 1.1, 1.0)),
      (2, Vectors.dense(3.0, 10.1, 3.0))
    )).toDF("id", "features")

    val scaler = new MinMaxScaler()
      .setInputCol("features")
      .setOutputCol("scaledFeatures")
    val scalerModel = scaler.fit(dataFrame)
    scalerModel.transform(dataFrame).show(false)

    println(scaler.getMin)
    /**
    |id |features      |scaledFeatures|
+---+--------------+--------------+
|0  |[1.0,0.1,-1.0]|[0.0,0.0,0.0] |
|1  |[2.0,1.1,1.0] |[0.5,0.1,0.5] |
|2  |[3.0,10.1,3.0]|[1.0,1.0,1.0] |
+---+--------------+--------------+
      */
  }
}
