package com.wzq.spark.ml.Classification

import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.classification.{DecisionTreeClassificationModel, DecisionTreeClassifier}
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.ml.feature.{IndexToString, StringIndexer, VectorIndexer}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SparkSession

object Decisiontree {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("mlib").setMaster("local[2]")
    val sc = new SparkContext(conf)
    val ss = SparkSession.builder().getOrCreate()
    val data = ss.read.format("libsvm").load("data/sample_libsvm_data.txt")
    //先对数据进行转换
    val labelIndexer = new StringIndexer()
      .setInputCol("label")
      .setOutputCol("indexedLabel")
      .fit(data)
    val featureIndexer = new VectorIndexer()
      .setInputCol("features")
      .setOutputCol("indexedFeatures")
      .setMaxCategories(4)
      .fit(data)
    val dt = new DecisionTreeClassifier()
      .setLabelCol("indexedLabel")
      .setFeaturesCol("indexedFeatures")
    val labelConverter = new IndexToString()
      .setInputCol("prediction")
      .setOutputCol("predictedLabel")
      .setLabels(labelIndexer.labels)
    val pipeline = new Pipeline()
      .setStages(Array(labelIndexer, featureIndexer, dt, labelConverter))
    val Array(trainingData, testData) = data.randomSplit(Array(0.7, 0.3))
    val model = pipeline.fit(trainingData)
    val predictions = model.transform(testData)
   // predictions.show()
    /**
    +-----+--------------------+------------+--------------------+-------------+-----------+----------+--------------+
|label|            features|indexedLabel|     indexedFeatures|rawPrediction|probability|prediction|predictedLabel|
+-----+--------------------+------------+--------------------+-------------+-----------+----------+--------------+
|  0.0|(692,[100,101,102...|         1.0|(692,[100,101,102...|   [0.0,28.0]|  [0.0,1.0]|       1.0|           0.0|
|  0.0|(692,[122,123,124...|         1.0|(692,[122,123,124...|   [0.0,28.0]|  [0.0,1.0]|       1.0|           0.0|
|  0.0|(692,[124,125,126...|         1.0|(692,[124,125,126...|   [0.0,28.0]|  [0.0,1.0]|       1.0|           0.0|
      */

    //模型评价
    val evalutor=new MulticlassClassificationEvaluator()
      .setLabelCol("indexedLabel")
      .setPredictionCol("prediction")
      .setMetricName("accuracy")
    val accuracy=evalutor.evaluate(predictions)
    //println("Test Error = " + (1.0 - accuracy))  //Test Error = 0.04166666666666663

    val treeModel = model.stages(2).asInstanceOf[DecisionTreeClassificationModel]
    println(treeModel.toDebugString)

    /**
    DecisionTreeClassificationModel (uid=dtc_135f4397a930) of depth 2 with 5 nodes
  If (feature 434 <= 0.0)
   If (feature 99 in {2.0})
    Predict: 0.0
   Else (feature 99 not in {2.0})
    Predict: 1.0
  Else (feature 434 > 0.0)
   Predict: 0.0
      */
  }
}
