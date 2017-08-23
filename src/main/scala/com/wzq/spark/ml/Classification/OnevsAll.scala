package com.wzq.spark.ml.Classification

import org.apache.spark.ml.classification.{LogisticRegression, OneVsRest}
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SparkSession

/**
  * 将一个给定的二分类算法有效地扩展到多分类问题应用中
  */
object OnevsAll extends App{
    val conf = new SparkConf().setAppName("mlib").setMaster("local[2]")
    val sc = new SparkContext(conf)
    val ss = SparkSession.builder().getOrCreate()
    val inputData = ss.read.format("libsvm").load("data/sample_multiclass_classification_data.txt")
    val Array(train, test) = inputData.randomSplit(Array(0.8, 0.2))
    //base classifier
    val classifier = new LogisticRegression()
      .setMaxIter(10)
      .setTol(1E-6)
      .setFitIntercept(true)
    val ovr = new OneVsRest().setClassifier(classifier)
    val ovrModel = ovr.fit(train)
    val predictions = ovrModel.transform(test)
    val evaluator = new MulticlassClassificationEvaluator()
      .setMetricName("accuracy")
    val accuracy = evaluator.evaluate(predictions)
    println(s"Test Error = ${1 - accuracy}")  //Test Error = 0.03125
}
