package com.wzq.spark.ml.feature.transformers

import org.apache.spark.ml.feature.StandardScaler
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SparkSession

/**
  * 对每个特征规范化
  *  unit standard deviation
  *  zero mean ：将某个特征向量进行标准化，使数据均值为0，方差为1
  *  dense output
  */
object StandardScaler {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("mlib").setMaster("local[2]")
    val sc = new SparkContext(conf)
    val ss = SparkSession.builder().getOrCreate()
    val dataFrame = ss.read.format("libsvm").load("data/sample_libsvm_data.txt")

    val scaler = new StandardScaler()
      .setInputCol("features")
      .setOutputCol("scaledFeatures")
      .setWithStd(true)  //将方差缩放到1
      .setWithMean(false)  //将均值移到0,当数据为稀疏矩阵，必须设置为false
    val scalerModel = scaler.fit(dataFrame)
    val scaledData = scalerModel.transform(dataFrame)
    scaledData.show(false)
  }
}
