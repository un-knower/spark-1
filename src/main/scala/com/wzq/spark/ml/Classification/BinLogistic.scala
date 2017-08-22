package com.wzq.spark.ml.Classification

import org.apache.spark.ml.classification.{BinaryLogisticRegressionSummary, LogisticRegression}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.max

object BinLogistic {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("mlib").setMaster("local[2]")
    val sc = new SparkContext(conf)
    val ss = SparkSession.builder().getOrCreate()
    val training = ss.read.format("libsvm").load("data/sample_libsvm_data.txt")
    val lr = new LogisticRegression()
      .setMaxIter(10)
      .setRegParam(0.3) //regularization parameter.
      .setElasticNetParam(0.8) //penalty
    val lrModel = lr.fit(training)

    //lrModel.transform(training).show(false)
    //系数和截距
    /*println("Coefficients:",lrModel.coefficients)
    println("Coefficients:",lrModel.coefficientMatrix)
    println("Intercept:",lrModel.intercept)
    println("Intercept:",lrModel.interceptVector)*/

    //Multinomial logistic regression can be used for binary classification by setting the family param to “multinomial”
    val mlr = new LogisticRegression()
      .setMaxIter(10)
      .setRegParam(0.3)
      .setElasticNetParam(0.8)
      .setFamily("multinomial")
    val mlrModel = mlr.fit(training)
    //mlrModel.transform(training).show(false)
    /*    println("Multinomial  Coefficients:",mlrModel.coefficientMatrix)
    println("Multinomial  Intercept:",mlrModel.interceptVector)*/
  }
}