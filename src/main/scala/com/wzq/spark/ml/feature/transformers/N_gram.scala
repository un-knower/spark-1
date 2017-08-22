package com.wzq.spark.ml.feature.transformers

import org.apache.spark.ml.feature.NGram
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SparkSession

/**
  * N元模型:transform input features into n-grams.
  * 预计或者评估一个句子是否合理
  * 用来评估两个字符串之间的差异程度。模糊匹配中常用的一种手段
  */
object N_gram {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("mlib").setMaster("local[2]")
    val sc = new SparkContext(conf)
    val ss = SparkSession.builder().getOrCreate()
    val wordDataFrame = ss.createDataFrame(Seq(
      (0, Array("Hi", "I", "heard", "about", "Spark")),
      (1, Array("I", "wish", "Java", "could", "use", "case", "classes")),
      (2, Array("Logistic", "regression", "models", "are", "neat"))
    )).toDF("id", "words")
    //N:指定词语长度
    val ngram = new NGram().setN(2).setInputCol("words").setOutputCol("ngrams")
    val ngramDataFrame = ngram.transform(wordDataFrame)
    ngramDataFrame.show(false)

    /**
      * 输出的结果是以空格分隔的字符串组成的集合
    id |words                                     |ngrams                                                            |
+---+------------------------------------------+------------------------------------------------------------------+
|0  |[Hi, I, heard, about, Spark]              |[Hi I, I heard, heard about, about Spark]                         |
|1  |[I, wish, Java, could, use, case, classes]|[I wish, wish Java, Java could, could use, use case, case classes]|
|2  |[Logistic, regression, models, are, neat] |[Logistic regression, regression models, models are, are neat]    |

      */
  }
}
