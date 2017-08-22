package com.wzq.spark.ml.pipeline

import org.apache.spark.ml.classification.{LogisticRegression, LogisticRegressionModel}
import org.apache.spark.ml.linalg.{Vector, Vectors}
import org.apache.spark.ml.param.ParamMap
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.{Row, SparkSession}


object EstimatorTransformerParamExample {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("mlib").setMaster("local[2]")
    val sc = new SparkContext(conf)
    val ss = SparkSession.builder().getOrCreate()

    //1.创建(label, features)形式的数据集
    val training=ss.createDataFrame(
      Seq(
        (1.0, Vectors.dense(0.0, 1.1, 0.1)),
        (0.0, Vectors.dense(2.0, 1.0, -1.0)),
        (0.0, Vectors.dense(2.0, 1.3, 1.0)),
        (1.0, Vectors.dense(0.0, 1.2, -0.5))
      )
    ).toDF("label","features")

    //2.创建estimator
    val lr=new LogisticRegression()
    //打印出estimator参数和默认值
    //println("LogisticRegression parameters:\n" + lr.explainParams() + "\n")

    //3.修改默认参数值
    lr.setMaxIter(10).setRegParam(0.01)

    //4.建立模型
    val model1=lr.fit(training)
    //打印出模型参数，(name: value)形式，其中name为这个实例的唯一id
   // println("Model 1 was fit using parameters: " + model1.parent.extractParamMap)

    //除了上面这种设置参数的方法外，还可以通过ParamMap制定多个参数,此时会覆盖前面使用set方法设置的参数
    val paramMap=ParamMap().put(lr.maxIter->30).put(lr.regParam->0.1,lr.threshold->0.55)
    val paramMap2 = ParamMap(lr.probabilityCol -> "myProbability")
    val paramMapCombined = paramMap ++ paramMap2

    val model2=lr.fit(training,paramMapCombined)
    //println("Model 2 was fit using parameters: " + model2.parent.extractParamMap)

    val test = ss.createDataFrame(Seq(
      (1.0, Vectors.dense(-1.0, 1.5, 1.3)),
      (0.0, Vectors.dense(3.0, 2.0, -0.1)),
      (1.0, Vectors.dense(0.0, 2.2, -1.5))
    )).toDF("label", "features")

    //保存模型
   // model1.write.overwrite().save("hdfs://192.168.94.6:9000/modeltest")

    //对训练数据进行预测时使用transform方法
/*    model2.transform(test)
      .select("features", "label", "myProbability", "prediction").collect()
      .foreach {
        case Row(features: Vector, label: Double, prob: Vector, prediction: Double) =>
        println(s"($features, $label) -> prob=$prob, prediction=$prediction")
          //([-1.0,1.5,1.3], 1.0) -> prob=[0.05707304171033977,0.9429269582896603], prediction=1.0
      }*/
    model2.transform(test).show(false)

    /**
    +-----+--------------+----------------------------------------+----------------------------------------+----------+
|label|features      |rawPrediction                           |myProbability                           |prediction|
+-----+--------------+----------------------------------------+----------------------------------------+----------+
|1.0  |[-1.0,1.5,1.3]|[-2.8046569418746508,2.8046569418746508]|[0.05707304171033977,0.9429269582896603]|1.0       |
      */
  }
}
