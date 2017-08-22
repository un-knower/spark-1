package com.wzq.spark.ml.feature.extractors

import org.apache.spark.ml.feature.{HashingTF, IDF, Tokenizer}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SparkSession

/**
  * Term frequency-inverse document frequency
  * 用以评估一字词对于一个文件集或一个语料库中的其中一份文件的重要程度
  * TF:term frequency，某一个给定的词语在该文件中出现的次数
  * IDF:inverse document frequency，一个词语普遍重要性的度量
  * 某一特定文件内的高词语频率，以及该词语在整个文件集合中的低文件频率，可以产生出高权重的TF-IDF
  *
  * In MLlib, we separate TF and IDF to make them flexible.
  * TF: HashingTF and CountVectorizer
  * IDF:是extimator，fit on a dataset and produces an IDFModel.
  */

object TF_IDF {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("mlib").setMaster("local[2]")
    val sc = new SparkContext(conf)
    val ss = SparkSession.builder().getOrCreate()
    val sentenceData = ss.createDataFrame(Seq(
      (0.0, "Hi I heard about Spark"),
      (0.0, "I wish Java could use case classes"),
      (1.0, "Logistic regression models are neat")
    )).toDF("label", "sentence")

    // Tokenizer:split each sentence into words
    val tokenizer = new Tokenizer().setInputCol("sentence").setOutputCol("words")
    val wordsData = tokenizer.transform(sentenceData)

    val hashingTF = new HashingTF()
      .setInputCol(tokenizer.getOutputCol).setOutputCol("rawFeatures").setNumFeatures(20)
    val featurizedData = hashingTF.transform(wordsData)

    val idf = new IDF().setInputCol(hashingTF.getOutputCol).setOutputCol("features")
    val idfModel = idf.fit(featurizedData)

    idfModel.transform(featurizedData).show(false)

    /**
    +-----+-----------------------------------+------------------------------------------+-----------------------------------------+----------------------------------------------------------------------------------------------------------------------+
|label|sentence                           |words                                     |rawFeatures                              |features                                                                                                              |
+-----+-----------------------------------+------------------------------------------+-----------------------------------------+----------------------------------------------------------------------------------------------------------------------+
|0.0  |Hi I heard about Spark             |[hi, i, heard, about, spark]              |(20,[0,5,9,17],[1.0,1.0,1.0,2.0])        |(20,[0,5,9,17],[0.6931471805599453,0.6931471805599453,0.28768207245178085,1.3862943611198906])                        |
|0.0  |I wish Java could use case classes |[i, wish, java, could, use, case, classes]|(20,[2,7,9,13,15],[1.0,1.0,3.0,1.0,1.0]) |(20,[2,7,9,13,15],[0.6931471805599453,0.6931471805599453,0.8630462173553426,0.28768207245178085,0.28768207245178085]) |
|1.0  |Logistic regression models are neat|[logistic, regression, models, are, neat] |(20,[4,6,13,15,18],[1.0,1.0,1.0,1.0,1.0])|(20,[4,6,13,15,18],[0.6931471805599453,0.6931471805599453,0.28768207245178085,0.28768207245178085,0.6931471805599453])|
+-----+-----------------------------------+------------------------------------------+-----------------------------------------+----------------------------------------------------------------------------------------------------------------------+
      */
  }
}
