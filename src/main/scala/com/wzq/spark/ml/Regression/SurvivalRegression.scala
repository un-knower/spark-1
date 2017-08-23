package com.wzq.spark.ml.Regression

import org.apache.spark.ml.linalg.Vectors
import org.apache.spark.ml.regression.AFTSurvivalRegression
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SparkSession

/**
  * 生存分析: 实现加速失效时间模型Accelerated failure time (AFT) model，易于并行化
  * 描述生存时间对数的模型，通常被称为生存分析的对数线性模型
  */
object SurvivalRegression {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("mlib").setMaster("local[2]")
    val sc = new SparkContext(conf)
    val ss = SparkSession.builder().getOrCreate()
    val training = ss.createDataFrame(Seq(
      (1.218, 1.0, Vectors.dense(1.560, -0.605)),
      (2.949, 0.0, Vectors.dense(0.346, 2.158)),
      (3.627, 0.0, Vectors.dense(1.380, 0.231)),
      (0.273, 1.0, Vectors.dense(0.520, 1.151)),
      (4.199, 0.0, Vectors.dense(0.795, -0.226))
    )).toDF("label", "censor", "features")
    val quantileProbabilities = Array(0.3, 0.6)
    val aft = new AFTSurvivalRegression()
      .setQuantileProbabilities(quantileProbabilities)
      .setQuantilesCol("quantiles")

    val model = aft.fit(training)
    println(s"Coefficients: ${model.coefficients}")
    println(s"Intercept: ${model.intercept}")
    println(s"Scale: ${model.scale}")
    model.transform(training).show(false)
    /**
    +-----+------+--------------+------------------+---------------------------------------+
|label|censor|features      |prediction        |quantiles                              |
+-----+------+--------------+------------------+---------------------------------------+
|1.218|1.0   |[1.56,-0.605] |5.7189794876349636|[1.1603238947151586,4.995456010274733] |
|2.949|0.0   |[0.346,2.158] |18.07652118149563 |[3.667545845471803,15.789611866277887] |
|3.627|0.0   |[1.38,0.231]  |7.381861804239099 |[1.4977061305190849,6.447962612338964] |
|0.273|1.0   |[0.52,1.151]  |13.57761250142538 |[2.7547621481507076,11.859872224069786]|
|4.199|0.0   |[0.795,-0.226]|9.013097744073843 |[1.8286676321297732,7.872826505878383] |
+-----+------+--------------+------------------+---------------------------------------+
      */
  }
}
