package com.wzq.spark.ml.clustering

import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.clustering.{BisectingKMeans, KMeans}
import org.apache.spark.ml.feature.{IndexToString, StringIndexer, VectorIndexer}
import org.apache.spark.ml.linalg.Vectors
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SparkSession

/**
  * 先利用分层聚类寻找奇异点（就是某一个点单独作为一类的情况）
  * 再根据分类情况，结合聚类表中各类的差别，确定分类具体数目
  * 最后利用K均值聚类进行最终分类
  */
object K_Bisecting_means {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("mlib").setMaster("local[2]")
    val sc = new SparkContext(conf)
    val ss = SparkSession.builder().getOrCreate()
    val dataset = ss.read.format("libsvm").load("data/sample_kmeans_data.txt")
    val bkm = new BisectingKMeans().setSeed(1).setPredictionCol("bkmPrediction").fit(dataset)
    bkm.transform(dataset).show()
    val kmeans = new KMeans().setK(3).setSeed(1L).setFeaturesCol("features").fit(dataset)
  }
}
