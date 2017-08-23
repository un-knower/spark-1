package com.wzq.spark.ml.clustering

import org.apache.spark.ml.clustering.GaussianMixture
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SparkSession

/**
  * 与k-means不同的是在聚类过程给出分配到类的概率
  */
object GaussianMixture {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("mlib").setMaster("local[2]")
    val sc = new SparkContext(conf)
    val ss = SparkSession.builder().getOrCreate()
    val dataset = ss.read.format("libsvm").load("data/sample_kmeans_data.txt")
    val gmm = new GaussianMixture()
      .setK(2)
    val model = gmm.fit(dataset)
    model.transform(dataset).show()
    for (i <- 0 until model.getK) {
      println(s"Gaussian $i:\nweight=${model.weights(i)}\n" +
        s"mu=${model.gaussians(i).mean}\nsigma=\n${model.gaussians(i).cov}\n")
    }

    /**
    +-----+--------------------+----------+--------------------+
|label|            features|prediction|         probability|
+-----+--------------------+----------+--------------------+
|  0.0|           (3,[],[])|         0|[0.99999999999999...|
|  1.0|(3,[0,1,2],[0.1,0...|         0|[0.99999999999999...|
|  2.0|(3,[0,1,2],[0.2,0...|         0|[0.99999999999999...|
|  3.0|(3,[0,1,2],[9.0,9...|         1|[2.09399616965966...|
|  4.0|(3,[0,1,2],[9.1,9...|         1|[9.89133752128275...|
|  5.0|(3,[0,1,2],[9.2,9...|         1|[2.09399616966056...|
      */
  }
}
