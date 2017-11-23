package textClassify

import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.classification.NaiveBayes
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.ml.feature._
import org.apache.spark.sql.SparkSession

object BayesClassify_EN_lemma {
  def main(args: Array[String]): Unit = {
    val ss=SparkSession.builder().appName("bayes").master("local[3]")
      .getOrCreate()
    import ss.implicits._

    val bayesDF=ss.read.csv("data/sms_spam.csv")
      //.map(i=>(i(0).toString,i(1).toString.replaceAll("\\d+","")))
      //.map((i=>(i._1,i._2.replaceAll("\\pP"," "))))
      .toDF("label","sentence")

    val stopwords=new StopWordsRemover().getStopWords
    val lemma=bayesDF
      .map(i=>(i(0).toString,new Lemma().plainTextToLemmas(i(1).toString,stopwords)))
      .toDF("label","message")



    val indexer=new StringIndexer()
      .setInputCol("label")
      .setOutputCol("labelIndex")
      .fit(lemma)
    indexer.transform(lemma).show(20,false)
    val cvModel=new CountVectorizer()
      .setInputCol("message")
      .setOutputCol("rawFeatures")
      .setMinDF(5)

    val hashingTF = new HashingTF()
      .setInputCol("message")
      .setOutputCol("rawFeatures")

    val idfModel = new IDF()
      .setInputCol("rawFeatures")
      .setOutputCol("features")

    val normalizer = new Normalizer()
      .setInputCol("features")
      .setOutputCol("normalizedFeatures")
      .setP(2)

    val naiveBayes = new NaiveBayes()
      .setFeaturesCol("normalizedFeatures")
      .setLabelCol("labelIndex")
      .setPredictionCol("prediction")
      .setModelType("multinomial")
      .setSmoothing(1.0)

    val labelConverter = new IndexToString()
      .setInputCol("prediction")
      .setOutputCol("predictedLabel")
      .setLabels(indexer.labels)

    val Array(trainingData, testData) = lemma.randomSplit(Array(0.8, 0.2), seed = 1234L)

    val pipeline = new Pipeline().setStages(Array(indexer,hashingTF,idfModel,normalizer,naiveBayes,labelConverter))
    val model=pipeline.fit(trainingData)
    val testDF=model.transform(testData)

    //testDF.show(20)

    val evaluator=new MulticlassClassificationEvaluator()
      .setLabelCol("labelIndex")
      .setPredictionCol("prediction")
      .setMetricName("accuracy")

    /**
      * 准确率
      * cvModel：
      * tf-idf：
      */
    val predictionAccuracy=evaluator.evaluate(testDF)
    //println("Testing Accuracy is:"+predictionAccuracy)

  }
}
