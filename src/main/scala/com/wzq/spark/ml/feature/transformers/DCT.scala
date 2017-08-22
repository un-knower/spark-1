package com.wzq.spark.ml.feature.transformers

import org.apache.spark.ml.feature.DCT
import org.apache.spark.ml.linalg.Vectors
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SparkSession

/**
  * Discrete Cosine Transform ：离散余弦变换
  */
object DCT {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("mlib").setMaster("local[2]")
    val sc = new SparkContext(conf)
    val ss = SparkSession.builder().getOrCreate()
    val data = Seq(
      Vectors.dense(0.0, 1.0, -2.0, 3.0),
      Vectors.dense(-1.0, 2.0, 4.0, -7.0),
      Vectors.dense(14.0, -2.0, -5.0, 1.0))
    val df = ss.createDataFrame(data.map(Tuple1.apply)).toDF("features")
    val dct = new DCT()
      .setInputCol("features")
      .setOutputCol("featuresDCT")
      .setInverse(false)
    val dctDf = dct.transform(df)
    dctDf.show(false)

    /**
    |features            |featuresDCT                                                     |
+--------------------+----------------------------------------------------------------+
|[0.0,1.0,-2.0,3.0]  |[1.0,-1.1480502970952693,2.0000000000000004,-2.7716385975338604]|
|[-1.0,2.0,4.0,-7.0] |[-1.0,3.378492794482933,-7.000000000000001,2.9301512653149677]  |
|[14.0,-2.0,-5.0,1.0]|[4.0,9.304453421915744,11.000000000000002,1.5579302036357163]   |
      */
  }
}
