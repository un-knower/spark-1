package com.wzq.spark.ml.Classification

import org.apache.spark.sql.functions.max
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.ml.classification.{BinaryLogisticRegressionSummary, LogisticRegression}
import org.apache.spark.sql.SparkSession

object LogisticRegressionSummary {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("mlib").setMaster("local[2]")
    val sc = new SparkContext(conf)
    val ss = SparkSession.builder().getOrCreate()
    val training = ss.read.format("libsvm").load("data/sample_libsvm_data.txt")
    val lr = new LogisticRegression()
      .setMaxIter(10)
      .setRegParam(0.3)
      .setElasticNetParam(0.8)
    val lrModel = lr.fit(training)
    //Extract the summary from the returned LogisticRegressionModel instance
    val trainingSummary = lrModel.summary
    //(scaled loss + regularization) at each iteration
    val objectiveHistory = trainingSummary.objectiveHistory
    //println("objectiveHistory:")
    //objectiveHistory.foreach(loss => println(loss))

    //roc:评价模型好坏
    val binarySummary = trainingSummary.asInstanceOf[BinaryLogisticRegressionSummary]
    val roc = binarySummary.roc
    // roc.show(false)

    import ss.implicits._
    val fMeasure = binarySummary.fMeasureByThreshold
    //fMeasure.show()
    /**
    +------------------+--------------------+
|         threshold|           F-Measure|
+------------------+--------------------+
|0.7845860015371142|0.034482758620689655|
|0.7843193344168922| 0.06779661016949151|
|0.7842976092510131|                 0.1|
|0.7842531051133191| 0.13114754098360656|
      */
    val maxFMeasure = fMeasure.select(max("F-Measure")).head().getDouble(0)
    println(maxFMeasure)
    val bestThreshold = fMeasure.where($"F-Measure" === maxFMeasure)
     .select("threshold").head().getDouble(0)
    lrModel.setThreshold(bestThreshold)

    //fMeasure.select("threshold").where($"F-Measure".equalTo(max("F-Measure"))).show()

    println(lrModel.getThreshold) //0.5585022394278357
  }
}
