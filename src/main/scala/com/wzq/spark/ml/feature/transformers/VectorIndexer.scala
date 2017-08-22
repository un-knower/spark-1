package com.wzq.spark.ml.feature.transformers

import org.apache.spark.ml.feature.VectorIndexer
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SparkSession

/**
  *对数据集特征向量中的类别特征进行编号
  */
object VectorIndexer {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("mlib").setMaster("local[2]")
    val sc = new SparkContext(conf)
    val ss = SparkSession.builder().getOrCreate()
    val data = ss.read.format("libsvm").load("data/sample_libsvm_data.txt")

    val indexer = new VectorIndexer()
      .setInputCol("features")
      .setOutputCol("indexed")
      .setMaxCategories(10)
    val indexerModel = indexer.fit(data)
    indexerModel.transform(data).show(false)
  }
}
