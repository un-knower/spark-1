package com.wzq.spark.ml.feature.extractors

import org.apache.spark.ml.feature.Word2Vec
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SparkSession

/**
  * 将单词转换成向量形式的工具。
  * 可以把对文本内容的处理简化为向量空间中的向量运算，计算出向量空间上的相似度，来表示文本语义上的相似度。
  */
object Word2vec {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("mlib").setMaster("local[2]")
    val sc = new SparkContext(conf)
    val ss = SparkSession.builder().getOrCreate()

    //Tuple1.apply: Seq[(Array[String])] ==>> List((Array(hi, i),))
    val documentDF = ss.createDataFrame(Seq(
      "Hi I heard about Spark".split(" "),
      "I wish Java could use case classes".split(" "),
      "Logistic regression models are neat".split(" ")
    ).map(Tuple1.apply)).toDF("text")
    val word2Vec = new Word2Vec()
      .setInputCol("text")
      .setOutputCol("result")
      .setVectorSize(3)
      .setMinCount(0)
    val model = word2Vec.fit(documentDF)
    model.transform(documentDF).show(false)

    /**
    +------------------------------------------+---------------------------------------------------------------+
|text                                      |result                                                         |
+------------------------------------------+---------------------------------------------------------------+
|[Hi, I, heard, about, Spark]              |[0.03173386193811894,0.009443491697311401,0.024377789348363876]|
|[I, wish, Java, could, use, case, classes]|[0.025682436302304268,0.0314303718706859,-0.01815584538105343] |
|[Logistic, regression, models, are, neat] |[0.022586782276630402,-0.01601201295852661,0.05122732147574425]|
+------------------------------------------+---------------------------------------------------------------+
      */
  }
}
