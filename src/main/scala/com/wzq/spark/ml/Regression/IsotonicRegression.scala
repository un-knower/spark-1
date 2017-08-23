package com.wzq.spark.ml.Regression

import org.apache.spark.ml.regression.IsotonicRegression
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SparkSession

/**
  * 保序回归
  * 一种单调递增函数的回归，回归模型中后一个x一定比前一个x大，也就是有序
  */
object IsotonicRegression {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("mlib").setMaster("local[2]")
    val sc = new SparkContext(conf)
    val ss = SparkSession.builder().getOrCreate()
    val dataset = ss.read.format("libsvm")
      .load("data/sample_isotonic_regression_libsvm_data.txt")
    val ir = new IsotonicRegression()
    val model = ir.fit(dataset)
    println(s"Boundaries in increasing order: ${model.boundaries}\n")
    println(s"Predictions associated with the boundaries: ${model.predictions}\n")
    model.transform(dataset).show()
  }
}
