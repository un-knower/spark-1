package com.wzq.spark.ml.pipeline

import org.apache.spark.ml.{Pipeline, PipelineModel}
import org.apache.spark.ml.classification.LogisticRegression
import org.apache.spark.ml.feature.{HashingTF, Tokenizer}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.{Row, SparkSession}
import org.apache.spark.ml.linalg.Vector

object PipelineExample {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("mlib").setMaster("local[2]")
    val sc = new SparkContext(conf)
    val ss = SparkSession.builder().getOrCreate()

    val training = ss.createDataFrame(Seq(
      (0L, "a b c d e spark", 1.0),
      (1L, "b d", 0.0),
      (2L, "spark f g h", 1.0),
      (3L, "hadoop mapreduce", 0.0)
    )).toDF("id", "text", "label")

    /**
      * pipeline：运行一组算法处理dataset，由一组Transformers and Estimators组成，并且按照顺序执行
      *Transformers：包括 feature transformers and learned models，实现 transform()方法，将一个dataframe转换为另一个dataframe
      * Estimators： algorithm that fits  on data，实现fit()方法，接受一个dataframe，转换为一个model即一个Transformer
      * 每个Transformer or Estimator有一个唯一的id，在指定参数时有用
      * 每个Transformer or Estimator实例不能插入pipeline两次，因为pipeline的stages有唯一id
      */

    //创建一个ML pipeline，由3个阶段组成：tokenizer, hashingTF, and lr

    /**
      * 1.tokenizer:分解器  将原始文件分割成words，并添加一列words列
      * 2.HashingTF:将words coulmn转换为数字的特征向量feature vectors
      * 3.LogisticRegression：学习预测模型
      */

    val tokenizer=new Tokenizer().setInputCol("text").setOutputCol("words")
    val hashingTF = new HashingTF()
      .setNumFeatures(1000)
      .setInputCol(tokenizer.getOutputCol)
      .setOutputCol("features")
    val lr = new LogisticRegression()
      .setMaxIter(10)
      .setRegParam(0.001)
    val pipeline=new Pipeline().setStages(Array(tokenizer, hashingTF, lr))
    val model=pipeline.fit(training)

    val test = ss.createDataFrame(Seq(
      (4L, "spark i j k"),
      (5L, "l m n"),
      (6L, "spark hadoop spark"),
      (7L, "apache hadoop")
    )).toDF("id", "text")

    //保存和加载
    //model.write.overwrite().save("hdfs://192.168.94.6:9000/modeltest")
    //val loadmodel=PipelineModel.load("")

    model.transform(training).show(false)
/*    model.transform(test)
      .select("id", "text", "probability", "prediction")
      .collect()
      .foreach { case Row(id: Long, text: String, prob: Vector, prediction: Double) =>
        println(s"($id, $text) --> prob=$prob, prediction=$prediction")*/

        /**
(4, spark i j k) --> prob=[0.15964077387874118,0.8403592261212589], prediction=1.0
(5, l m n) --> prob=[0.8378325685476612,0.16216743145233875], prediction=0.0
(6, spark hadoop spark) --> prob=[0.06926633132976273,0.9307336686702373], prediction=1.0
(7, apache hadoop) --> prob=[0.9821575333444208,0.01784246665557917], prediction=0.0
          */
      }
}

