package com.wzq.spark.ml.tuning

import org.apache.spark.ml.evaluation.RegressionEvaluator
import org.apache.spark.ml.regression.LinearRegression
import org.apache.spark.ml.tuning.{ParamGridBuilder, TrainValidationSplit}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SparkSession

/**
  * Created by wangzhiqun on 2017/8/16.
  */

/**
  * hyper-parameter tuning
  *只产生1个训练数据与测试数据对
  * 低成本，但当训练数据集较小时，不能得到可靠结果
  */
object Train_Validation {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("mlib").setMaster("local[2]")
    val sc = new SparkContext(conf)
    val ss = SparkSession.builder().getOrCreate()
    val path=this.getClass.getClassLoader.getResource("sample_linear_regression_data.txt").getPath
    val data=ss.read.format("libsvm").load(path).cache()

    //准备训练数据与测试数据
    val Array(training, test) = data.randomSplit(Array(0.9, 0.1), seed = 12345)
    val lr = new LinearRegression().setMaxIter(10)

    //建立参数网
    val paramGrid = new ParamGridBuilder()
      .addGrid(lr.regParam, Array(0.1, 0.01))
      .addGrid(lr.fitIntercept)
      .addGrid(lr.elasticNetParam, Array(0.0, 0.5, 1.0))
      .build()

    //利用最佳参数
    val trainValidationSplit = new TrainValidationSplit()
      .setEstimator(lr)
      .setEvaluator(new RegressionEvaluator)
      .setEstimatorParamMaps(paramGrid)
      // 80% of the data will be used for training and the remaining 20% for validation.
      .setTrainRatio(0.8)

    val model = trainValidationSplit.fit(training)

    model.transform(test).show()
  }
}
