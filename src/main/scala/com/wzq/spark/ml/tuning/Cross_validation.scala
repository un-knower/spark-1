package com.wzq.spark.ml.tuning

import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.classification.LogisticRegression
import org.apache.spark.ml.evaluation.BinaryClassificationEvaluator
import org.apache.spark.ml.feature.{HashingTF, Tokenizer}
import org.apache.spark.ml.tuning.{CrossValidator, ParamGridBuilder}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SparkSession

/**
  * Created by wangzhiqun on 2017/8/16.
  */

/**
  * 需要3个步骤
  * 1.Estimator: algorithm or Pipeline to tune
  * 2.参数ParamMap：对Estimator应用这些参数
  * 3.Evaluator：评价模型好坏 RegressionEvaluator、 BinaryClassificationEvaluator、 MulticlassClassificationEvaluator
  */

/**
1.CrossValidator:交叉验证
如当k＝3时，CrossValidator产生3个训练数据与测试数据对，每个数据对使用2/3的数据来训练，1/3的数据来测试
对于一组特定的参数表,计算基于三组不同训练数据与测试数据对训练得到的模型的评估准则的平均值
确定最佳参数表后，最后使用最佳参数表基于全部数据来重新拟合估计器。
2.使用CrossValidator代价很大
一般选择K=3或 K=10
  */
object Cross_validation {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("mlib").setMaster("local[2]")
    val sc = new SparkContext(conf)
    val ss = SparkSession.builder().getOrCreate()
    val training = ss.createDataFrame(Seq(
      (0L, "a b c d e spark", 1.0),
      (1L, "b d", 0.0),
      (2L, "spark f g h", 1.0),
      (3L, "hadoop mapreduce", 0.0),
      (4L, "b spark who", 1.0),
      (5L, "g d a y", 0.0),
      (6L, "spark fly", 1.0),
      (7L, "was mapreduce", 0.0),
      (8L, "e spark program", 1.0),
      (9L, "a e c l", 0.0),
      (10L, "spark compile", 1.0),
      (11L, "hadoop software", 0.0)
    )).toDF("id", "text", "label")

    val tokenizer = new Tokenizer()
      .setInputCol("text")
      .setOutputCol("words")
    val hashingTF = new HashingTF()
      .setInputCol(tokenizer.getOutputCol)
      .setOutputCol("features")
    val lr = new LogisticRegression()
      .setMaxIter(10)
    val pipeline = new Pipeline()
      .setStages(Array(tokenizer, hashingTF, lr))

    /**
      * ParamGridBuilder:创建包含多个参数的网格
      * CrossValidator将有 3 x 2 = 6 parameter供选择
      */
    val paramGrid=new ParamGridBuilder()
      .addGrid(hashingTF.numFeatures,Array(10,100,1000))
      .addGrid(lr.regParam,Array(0.1,0.01))
      .build()

    /**
      * estimator:pipeline
      * parammaps:paramGrid
      * evaluator:BinaryClassificationEvaluator
      */
    val cv=new CrossValidator()
      .setEstimator(pipeline)
      .setEvaluator(new BinaryClassificationEvaluator())
      .setEstimatorParamMaps(paramGrid)
      .setNumFolds(2)

    val cvModel=cv.fit(training)

    val test = ss.createDataFrame(Seq(
      (4L, "spark i j k"),
      (5L, "l m n"),
      (6L, "mapreduce spark"),
      (7L, "apache hadoop")
    )).toDF("id", "text")

    // cvModel uses the best model found
    cvModel.transform(test).show(false)

    /**
    +---+---------------+------------------+------------------------------------+----------------------------------------+----------------------------------------+----------+
|id |text           |words             |features                            |rawPrediction                           |probability                             |prediction|
+---+---------------+------------------+------------------------------------+----------------------------------------+----------------------------------------+----------+
|4  |spark i j k    |[spark, i, j, k]  |(100,[5,29,49,56],[1.0,1.0,1.0,1.0])|[-1.056032273315308,1.056032273315308]  |[0.25806842225846466,0.7419315777415353]|1.0       |

      */
  }
}
