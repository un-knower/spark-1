package com.wzq.spark.ml.CollaborativeFiltering

import org.apache.spark.ml.evaluation.RegressionEvaluator
import org.apache.spark.ml.recommendation.ALS
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SparkSession

/**
  * 交替最小二乘法
  * 用户和商品的关系，可以抽象为如下的三元组：<User,Item,Rating>
  * explicit feedback:评分
  * implicit feedback：间接反映用户喜好，如搜索记录、关键字、点击
  */

object Alternating_least_squares {

  case class Rating(userId: Int, movieId: Int, rating: Float, timestamp: Long)
  def parseRating(str: String): Rating = {
    val fields = str.split("::")
    assert(fields.size == 4)
    Rating(fields(0).toInt, fields(1).toInt, fields(2).toFloat, fields(3).toLong)
  }
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("mlib").setMaster("local[2]")
    val sc = new SparkContext(conf)
    val ss = SparkSession.builder().getOrCreate()
    import ss.implicits._
    val ratings = ss.read.textFile("data/sample_movielens_ratings.txt")
      .map(parseRating)
      .as[Rating]
    val Array(training, test) = ratings.randomSplit(Array(0.8, 0.2))
    val als = new ALS()
      .setMaxIter(5)
      .setRegParam(0.01)
      //.setImplicitPrefs(true)   使用隐式反馈
      .setUserCol("userId")
      .setItemCol("movieId")
      .setRatingCol("rating")
    val model = als.fit(training)
    val predictions = model.transform(test)
    //predictions.show()
    /**
    +------+-------+------+----------+-----------+
|userId|movieId|rating| timestamp| prediction|
+------+-------+------+----------+-----------+
|    24|     31|   1.0|1424380312|0.011237949|
|     0|     31|   1.0|1424380312|  1.1075479|
|    18|     31|   1.0|1424380312| 0.12606186|
|    20|     85|   2.0|1424380312|  2.1029162|
|     4|     85|   1.0|1424380312|  4.0158124|
      */
    val evaluator = new RegressionEvaluator()
      .setMetricName("rmse")
      .setLabelCol("rating")
      .setPredictionCol("prediction")
    val rmse = evaluator.evaluate(predictions)
    println(s"Root-mean-square error = $rmse")  //Root-mean-square error = 1.7179592436315958
  }
}
