package textClassify

import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.classification.{NaiveBayes, NaiveBayesModel}
import org.apache.spark.ml.evaluation. MulticlassClassificationEvaluator
import org.apache.spark.ml.feature._
import org.apache.spark.sql.SparkSession


object BayesClassify_EN {
  def main(args: Array[String]): Unit = {
    val ss=SparkSession.builder().appName("bayes").master("local[3]")
      .getOrCreate()
    import ss.implicits._

    val bayesDF=ss.read.csv("data/sms_spam.csv")
        .map(i=>(i(0).toString,i(1).toString.replaceAll("\\d+"," ")))
        .map((i=>(i._1,i._2.replaceAll("\\pP"," "))))
        .toDF("label","sentence")

    /**
      * 朴素贝叶斯：基于假设数据集的所有特征具有相同的重要性和独立性。适用于文本分类
      * 1.Tokenizer:将文本拆分成单词
      * 2.StringIndexer:将字符串标签编码成索引分类标签
      * 3.CountVectorizer:通过计数将文本文档转换为特征向量,统计TF词频。或者使用HashingTF
      * 4.IDF:对特征向量集（一般由HashingTF或CountVectorizer产生）做取对数处理，特征词出现的文档越多，权重越低
      * 5.Normalizer:将一组特征向量（通过计算p-范数）规范化,使输入数据标准化
      * 6.NaiveBayes:训练模型
      * 7.IndexToString:将索引化标签还原成原始字符串
      * 8.Pipeline:指定每一个stage处理的column名，再添加生成的数据到新的一列，避免了每次都要处理数据使它们符合中间模型的输入结构
      * 9.MultiClassificationEvaluator:
      * 10.BinaryClassificationEvaluator:
      * 11.最后产生的列为：|label|sentence|words|labelIndex|rawFeatures|features|normalizedFeatures|rawPrediction|probability|prediction|predictedLabel|
      */


      //前提：注意需要将字符串转换为单词数组，分词，才能进行下一步向量操作
    //string to lowercase and then splits it by white spaces，默认分隔符·正则表达式（"\s+"），一到多个空白符
    val tokenizer = new Tokenizer().setInputCol("sentence").setOutputCol("words")
/*    val regexTokenizer = new RegexTokenizer()
      .setInputCol("sentence")
      .setOutputCol("words")
      .setPattern("")
      .setGaps(false)*/

    //索引标签范围[0, numLabels),按照标签出现频率排序，出现最多的标签索引为0
    val indexer=new StringIndexer()
      .setInputCol("label")
      .setOutputCol("labelIndex")
      .fit(bayesDF)

    val remover=new StopWordsRemover()
      .setInputCol("words")
      .setOutputCol("message")

    //模型产生稀疏特征向量，根据语料库中的词频排序选出前vocabSize个词
    val cvModel=new CountVectorizer()
      .setInputCol("message")
      .setOutputCol("rawFeatures")
     // .setVocabSize(1)  //根据语料库中的词频排序选出前vocabSize个词
      .setMinDF(5)  //指定词汇表中的词语在文档中出现的最小次数

    val hashingTF = new HashingTF()
      .setInputCol("message")
      .setOutputCol("rawFeatures")

    //权重评估器
    val idfModel = new IDF()
      .setInputCol("rawFeatures")
      .setOutputCol("features")

    //p-范数
    val normalizer = new Normalizer()
      .setInputCol("features")
      .setOutputCol("normalizedFeatures")
      .setP(2)

    //
    val naiveBayes = new NaiveBayes()
      .setFeaturesCol("normalizedFeatures")
      .setLabelCol("labelIndex")
      .setPredictionCol("prediction")
      .setModelType("multinomial")  //多项式模型
      .setSmoothing(1.0)  //平滑参数,默认为1。拉普拉斯平滑

    //先通过StringIndexer产生索引化标签，然后使用索引化标签进行训练，最后再对预测结果使用IndexToString来获取其原始的标签字符串
    val labelConverter = new IndexToString()
      .setInputCol("prediction")
      .setOutputCol("predictedLabel")
      .setLabels(indexer.labels)

    val Array(trainingData, testData) = bayesDF.randomSplit(Array(0.8, 0.2), seed = 1234L)

    val pipeline = new Pipeline().setStages(Array(tokenizer,indexer,remover,hashingTF,idfModel,normalizer,naiveBayes,labelConverter))
    val model=pipeline.fit(trainingData)
    val testDF=model.transform(testData)

    testDF.show(20,false)
    //将预测结果写入文件
    //testDF.createTempView("t")
    //ss.sql("select sentence,predictedLabel from t where predictedLabel='ham'").write.csv("data/prediction.csv")

    //模型参数
/*    val modelParameters=model.stages(5).asInstanceOf[NaiveBayesModel]
    println(modelParameters.pi)
    println(modelParameters.theta)
    println("类的数量: "+modelParameters.numClasses)
    println("模型所接受的特征的数量: "+modelParameters.numFeatures)*/

    //多分类作用于2个列： prediction  and label
    val evaluator=new MulticlassClassificationEvaluator()
      .setLabelCol("labelIndex")  //原始标签
      .setPredictionCol("prediction")  //模型预测标签
      .setMetricName("accuracy")  //

    /**
      * 准确率
      * cvModel：0.9674502712477396<-->0.976491862567812(去除数字和标点符号)
      * tf-idf：0.8960216998191681<-->0.9168173598553345(去除数字和标点符号)
       */
    val predictionAccuracy=evaluator.evaluate(testDF)
    println("Testing Accuracy is:"+predictionAccuracy)

  }
}
