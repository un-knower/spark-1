package com.wzq.spark.ml.Classification

import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.classification.{GBTClassificationModel, GBTClassifier}
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.ml.feature.{IndexToString, StringIndexer, VectorIndexer}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SparkSession

object Gradient_boosted_tree {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("mlib").setMaster("local[2]")
    val sc = new SparkContext(conf)
    val ss = SparkSession.builder().getOrCreate()
    val data = ss.read.format("libsvm").load("data/sample_libsvm_data.txt")
    val labelIndexer = new StringIndexer()
      .setInputCol("label")
      .setOutputCol("indexedLabel")
      .fit(data)
    val featureIndexer = new VectorIndexer()
      .setInputCol("features")
      .setOutputCol("indexedFeatures")
      .setMaxCategories(4)
      .fit(data)
    val gbt = new GBTClassifier()
      .setLabelCol("indexedLabel")
      .setFeaturesCol("indexedFeatures")
      .setMaxIter(10)
    val labelConverter = new IndexToString()
      .setInputCol("prediction")
      .setOutputCol("predictedLabel")
      .setLabels(labelIndexer.labels)
    val pipeline = new Pipeline()
      .setStages(Array(labelIndexer, featureIndexer, gbt, labelConverter))
    val Array(trainingData, testData) = data.randomSplit(Array(0.7, 0.3))
    val model = pipeline.fit(trainingData)
    val predictions = model.transform(testData)
    predictions.show()
    /**
    +-----+--------------------+------------+--------------------+----------+--------------+
|label|            features|indexedLabel|     indexedFeatures|prediction|predictedLabel|
+-----+--------------------+------------+--------------------+----------+--------------+
|  0.0|(692,[95,96,97,12...|         1.0|(692,[95,96,97,12...|       1.0|           0.0|
|  0.0|(692,[98,99,100,1...|         1.0|(692,[98,99,100,1...|       1.0|           0.0|
|  0.0|(692,[122,123,148...|         1.0|(692,[122,123,148...|       1.0|           0.0|
      */
    val evaluator = new MulticlassClassificationEvaluator()
      .setLabelCol("indexedLabel")
      .setPredictionCol("prediction")
      .setMetricName("accuracy")
    val accuracy = evaluator.evaluate(predictions)
    println("Test Error = " + (1.0 - accuracy))  //Test Error = 0.08108108108108103

    val gbtModel = model.stages(2).asInstanceOf[GBTClassificationModel]
    println(gbtModel.toDebugString)
  }
}
