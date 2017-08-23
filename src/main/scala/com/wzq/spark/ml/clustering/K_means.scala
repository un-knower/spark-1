package com.wzq.spark.ml.clustering

import org.apache.spark.ml.clustering.KMeans
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SparkSession

object K_means {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("mlib").setMaster("local[2]")
    val sc = new SparkContext(conf)
    val ss = SparkSession.builder().getOrCreate()
    val dataset = ss.read.format("libsvm").load("data/sample_kmeans_data.txt")
    val kmeans = new KMeans().setK(2).setSeed(1L)
    val model = kmeans.fit(dataset)
    val WSSSE = model.computeCost(dataset)
    println(s"Within Set Sum of Squared Errors = $WSSSE")
    println("Cluster Centers: ")
    model.clusterCenters.foreach(println)
    /**
    0.11999999999994547
    Cluster Centers:
[0.1,0.1,0.1]
[9.1,9.1,9.1]

      */
  }
}
