package com.wzq.spark.ml.Classification

import org.apache.spark.ml.classification.MultilayerPerceptronClassifier
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SparkSession

/**
  * 多层感知器MLP
  * 基于前馈人工神经网络(ANN)的分类器
  * 输出层中的节点数量N对应于类的数量
  */

object Multilayer_perceptron {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("mlib").setMaster("local[2]")
    val sc = new SparkContext(conf)
    val ss = SparkSession.builder().getOrCreate()
    val data = ss.read.format("libsvm").load("data/sample_multiclass_classification_data.txt")
    val splits = data.randomSplit(Array(0.6, 0.4), seed = 1234L)
    val train = splits(0)
    val test = splits(1)

    //隐藏层结点数=2n+1，n为输入结点数
    // 指定神经网络的图层,输入层4个节点；两个隐藏层隐藏结点数分别为5和4；输出层3个结点,3类
    val layers = Array[Int](4, 5, 4, 3)

    val trainer = new MultilayerPerceptronClassifier()
      .setLayers(layers)
      .setBlockSize(128)
      .setSeed(1234L)
      .setMaxIter(100)
    val model = trainer.fit(train)
    val result = model.transform(test)
    //result.show(false)
    val evaluator = new MulticlassClassificationEvaluator()
      .setMetricName("accuracy")
    println("Test set accuracy = " + evaluator.evaluate(result))  //Test set accuracy = 0.9019607843137255
  }
}
