package com.wzq.spark.ml.Classification

import org.apache.spark.ml.classification.NaiveBayes
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SparkSession

object NaiveBayes {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("mlib").setMaster("local[2]")
    val sc = new SparkContext(conf)
    val ss = SparkSession.builder().getOrCreate()
    val data = ss.read.format("libsvm").load("data/sample_libsvm_data.txt")
    val Array(trainingData, testData) = data.randomSplit(Array(0.7, 0.3), seed = 1234L)
    val model = new NaiveBayes()
      .fit(trainingData)
    val predictions = model.transform(testData)
    predictions.show()
    val evaluator = new MulticlassClassificationEvaluator()
      .setLabelCol("label")
      .setPredictionCol("prediction")
      .setMetricName("accuracy")
    val accuracy = evaluator.evaluate(predictions)
    println("Test set accuracy = " + accuracy)  //Test set accuracy = 1.0
  }
}
