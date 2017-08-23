package com.wzq.spark.ml.Regression

import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.evaluation.RegressionEvaluator
import org.apache.spark.ml.feature.VectorIndexer
import org.apache.spark.ml.regression.{GBTRegressionModel, GBTRegressor}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SparkSession

object GBTRegressor {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("mlib").setMaster("local[2]")
    val sc = new SparkContext(conf)
    val ss = SparkSession.builder().getOrCreate()
    val data = ss.read.format("libsvm").load("data/sample_libsvm_data.txt")
    val featureIndexer = new VectorIndexer()
      .setInputCol("features")
      .setOutputCol("indexedFeatures")
      .setMaxCategories(4)
      .fit(data)
    val Array(trainingData, testData) = data.randomSplit(Array(0.7, 0.3))
    val gbt = new GBTRegressor()
      .setLabelCol("label")
      .setFeaturesCol("indexedFeatures")
      .setMaxIter(10)
    val pipeline = new Pipeline()
      .setStages(Array(featureIndexer, gbt))
    val model = pipeline.fit(trainingData)
    val predictions = model.transform(testData)
    predictions.show()
    /**
    +-----+--------------------+--------------------+----------+
|label|            features|     indexedFeatures|prediction|
+-----+--------------------+--------------------+----------+
|  0.0|(692,[122,123,148...|(692,[122,123,148...|       0.0|
|  0.0|(692,[123,124,125...|(692,[123,124,125...|       0.0|
|  0.0|(692,[124,125,126...|(692,[124,125,126...|       0.0|
      */
    val evaluator = new RegressionEvaluator()
      .setLabelCol("label")
      .setPredictionCol("prediction")
      .setMetricName("rmse")
    val rmse = evaluator.evaluate(predictions)
    println("Root Mean Squared Error (RMSE) on test data = " + rmse)

    val rfModel = model.stages(1).asInstanceOf[GBTRegressionModel]
    println("Learned regression forest model:\n" + rfModel.toDebugString)
  }
}
