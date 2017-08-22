package com.wzq.spark.ml.Classification

import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.classification.{RandomForestClassificationModel, RandomForestClassifier}
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.ml.feature.{IndexToString, StringIndexer, VectorIndexer}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SparkSession

object Randomforest {
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
    val rf = new RandomForestClassifier()
      .setLabelCol("indexedLabel")
      .setFeaturesCol("indexedFeatures")
      .setNumTrees(10)
    val labelConverter = new IndexToString()
      .setInputCol("prediction")
      .setOutputCol("predictedLabel")
      .setLabels(labelIndexer.labels)
    val pipeline = new Pipeline()
      .setStages(Array(labelIndexer, featureIndexer, rf, labelConverter))
    val Array(trainingData, testData) = data.randomSplit(Array(0.7, 0.3))
    val model = pipeline.fit(trainingData)
    val predictions = model.transform(testData)
    predictions.show()
    /**
    +-----+--------------------+------------+--------------------+-------------+-----------+----------+--------------+
|label|            features|indexedLabel|     indexedFeatures|rawPrediction|probability|prediction|predictedLabel|
+-----+--------------------+------------+--------------------+-------------+-----------+----------+--------------+
|  0.0|(692,[124,125,126...|         1.0|(692,[124,125,126...|   [0.0,10.0]|  [0.0,1.0]|       1.0|           0.0|
|  0.0|(692,[126,127,128...|         1.0|(692,[126,127,128...|    [1.0,9.0]|  [0.1,0.9]|       1.0|           0.0|
|  0.0|(692,[126,127,128...|         1.0|(692,[126,127,128...|    [1.0,9.0]|  [0.1,0.9]|       1.0|           0.0|
      */
    val evalutor=new MulticlassClassificationEvaluator()
      .setLabelCol("indexedLabel")
      .setPredictionCol("prediction")
      .setMetricName("accuracy")
    val accuracy=evalutor.evaluate(predictions)
    println("Test Error = " + (1.0 - accuracy))  //Test Error = 0.0

    val treeModel = model.stages(2).asInstanceOf[RandomForestClassificationModel]
    println(treeModel.toDebugString)
  }
}
