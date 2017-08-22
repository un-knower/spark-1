package com.wzq.spark.ml.feature.transformers

import org.apache.spark.ml.feature.{RegexTokenizer, Tokenizer}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SparkSession

import org.apache.spark.sql.functions._

/**
  * Created by wangzhiqun on 2017/8/21.
  */

/**
  * Tokenizer:split sentences into sequences of words.
  * RegexTokenizer: tokenization based on regular expression (regex) matching
  */
object Tokenization {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("mlib").setMaster("local[2]")
    val sc = new SparkContext(conf)
    val ss = SparkSession.builder().getOrCreate()
    val sentenceDataFrame = ss.createDataFrame(Seq(
      (0, "Hi I heard about Spark"),
      (1, "I wish Java could use case classes"),
      (2, "Logistic,regression,models,are,neat")
    )).toDF("id", "sentence")

    val tokenizer = new Tokenizer().setInputCol("sentence").setOutputCol("words")
    val regexTokenizer = new RegexTokenizer()
      .setInputCol("sentence")
      .setOutputCol("words")
      .setPattern("\\W")
    tokenizer.transform(sentenceDataFrame).show(false)
    regexTokenizer.transform(sentenceDataFrame).show(false)

    /**
    +---+-----------------------------------+------------------------------------------+
|id |sentence                           |words                                     |
+---+-----------------------------------+------------------------------------------+
|0  |Hi I heard about Spark             |[hi, i, heard, about, spark]              |
|1  |I wish Java could use case classes |[i, wish, java, could, use, case, classes]|
|2  |Logistic,regression,models,are,neat|[logistic,regression,models,are,neat]     |
      */
  }
}
