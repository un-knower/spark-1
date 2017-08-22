package com.wzq.spark.ml.feature.selectors

import org.apache.spark.ml.feature.ChiSqSelector
import org.apache.spark.ml.linalg.Vectors
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SparkSession

/**
  * 使用卡方检验来选择特征（降维），应用于标签数据
  */
object ChiSqSelector {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("mlib").setMaster("local[2]")
    val sc = new SparkContext(conf)
    val ss = SparkSession.builder().getOrCreate()
    val data = Seq(
      (7, Vectors.dense(0.0, 0.0, 18.0, 1.0), 1.0),
      (8, Vectors.dense(0.0, 1.0, 12.0, 0.0), 0.0),
      (9, Vectors.dense(1.0, 0.0, 15.0, 0.1), 0.0)
    )
    val df = ss.createDataFrame(data).toDF("id", "features", "clicked")
    val selector = new ChiSqSelector()
      //.setNumTopFeatures(1)  //使用卡方检验，将原始特征向量（特征数为4）降维（特征数为1）
       //.setFpr(0.5)
      .setFeaturesCol("features")
      .setLabelCol("clicked")
      .setOutputCol("selectedFeatures")
    val result = selector.fit(df).transform(df)
    result.show(false)

    /**
    ---+------------------+-------+----------------+
|id |features          |clicked|selectedFeatures|
+---+------------------+-------+----------------+
|7  |[0.0,0.0,18.0,1.0]|1.0    |[18.0]          |
|8  |[0.0,1.0,12.0,0.0]|0.0    |[12.0]          |
|9  |[1.0,0.0,15.0,0.1]|0.0    |[15.0]          |
+---+------------------+-------+----------------+
      */
  }
}
