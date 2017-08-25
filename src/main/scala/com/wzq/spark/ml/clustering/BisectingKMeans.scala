package com.wzq.spark.ml.clustering

import org.apache.spark.ml.clustering.BisectingKMeans
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SparkSession

/**
  * Bisecting k-means  二分k均值算法  属于分裂的层次聚类，采用自顶而下的策略
  * 首先将所有对象置于同一个簇中，然后细分为越来越小的簇，直到某个终止条件
  * k-means聚类算法的一个变体，主要是为了改进k-means算法随机选择初始质心的随机性造成聚类结果不确定性的问题
  * 而Bisecting k-means算法受随机选择初始质心的影响比较小
  */

/**
  * 另一种层次聚类属于凝聚，自下而上的策略，首先将每个对象看作一个簇，每一步合并相近的簇，直至终止条件
  */
object BisectingKMeans {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("mlib").setMaster("local[2]")
    val sc = new SparkContext(conf)
    val ss = SparkSession.builder().getOrCreate()
    val dataset = ss.read.format("libsvm").load("data/sample_kmeans_data.txt")
    val bkm = new BisectingKMeans().setK(2).setSeed(1)
    val model = bkm.fit(dataset)
 /*   val cost = model.computeCost(dataset)
    println(s"Within Set Sum of Squared Errors = $cost")
    println("Cluster Centers: ")
    val centers = model.clusterCenters
    centers.foreach(println)*/

    /**
    0.11999999999994547
    Cluster Centers:
[0.1,0.1,0.1]
[9.1,9.1,9.1]
      */
  }
}
