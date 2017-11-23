package textClassify

import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.classification.MultilayerPerceptronClassifier
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.ml.feature._
import org.apache.spark.sql.SparkSession

object Multilayer_PerceptronClassify {
  def main(args: Array[String]): Unit = {
    val ss=SparkSession.builder().appName("mp").master("local[3]")
      .getOrCreate()
    import ss.implicits._

    val mpDF=ss.read.csv("data/sms_spam.csv")
      .map(i=>(i(0).toString,i(1).toString.replaceAll("\\d+"," ")))
      .map((i=>(i._1,i._2.replaceAll("\\pP"," "))))
      .toDF("label","sentence")

    /**
      * 1.将文本句子转化成单词数组
      * 2.使用 Word2Vec 工具将单词数组转化成一个 K 维向量
      * 3.通过训练 K 维向量样本数据得到一个前馈神经网络模型(从输入层开始只接收前一层的输入，并把计算结果输出到后一层，并不会给前一层有所反馈，整个过程可以使用有向无环图来表示)，以此来实现文本的类别标签预测
      */
    val tokenizer = new Tokenizer().setInputCol("sentence").setOutputCol("words")
    val indexer=new StringIndexer()
      .setInputCol("label")
      .setOutputCol("labelIndex")
      .fit(mpDF)
    val remover=new StopWordsRemover()
      .setInputCol("words")
      .setOutputCol("message")
    val word2Vec = new Word2Vec()
      .setInputCol("message")
      .setOutputCol("features")
      .setMaxIter(100)
      .setVectorSize(100)
      .setMinCount(1) //只有当某个词出现的次数大于或者等于 minCount 时，才会被包含到词汇表里，否则会被忽略掉

    /**
      * 一个输入层——》一个或多个隐含层——》输出层
      * layer参数：数组类型参数，第一个元素与和特征向量的维度相等，最后一个元素需要训练数据的标签取值个数相等
      * 中间的元素有多少个就代表神经网络有多少个隐层，元素的取值代表了该层的神经元的个数
      */
    val layers = Array[Int](100,6,5,2)
    val mlpc = new MultilayerPerceptronClassifier()
      .setLayers(layers)
      .setFeaturesCol("features")
      .setLabelCol("labelIndex")
      .setPredictionCol("prediction")
    val labelConverter = new IndexToString()
      .setInputCol("prediction")
      .setOutputCol("predictedLabel")
      .setLabels(indexer.labels)

    val Array(trainingData, testData) = mpDF.randomSplit(Array(0.8, 0.2), seed = 1234L)

    val pipeline = new Pipeline().setStages(Array(tokenizer,indexer,remover,word2Vec,mlpc,labelConverter))
    val model=pipeline.fit(trainingData)
    val testDF=model.transform(testData)

    testDF.show(20,false)

    /**
      * 10次迭代10向量长度： 0.9493670886075949<-->0.9511754068716094(去除数字和标点符号)
      * 10次迭代100向量长度： 0.9511754068716094<-->0.972875226039783(去除数字和标点符号)
      * 100次迭代10向量长度： 0.9547920433996383<-->0.9647377938517179(去除数字和标点符号)
      * 100次迭代100向量长度： 0.9602169981916817<-->0.9647377938517179(去除数字和标点符号)
      */

    val evaluator=new MulticlassClassificationEvaluator()
      .setLabelCol("labelIndex")
      .setPredictionCol("prediction")
      .setMetricName("accuracy")
    val predictionAccuracy=evaluator.evaluate(testDF)
    println("Testing Accuracy is:"+predictionAccuracy)
  }
}
