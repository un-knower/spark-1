package clworks

import java.util.Properties

import org.apache.spark.ml.{Pipeline, PipelineModel}
import org.apache.spark.ml.classification.NaiveBayes
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.ml.feature.{CountVectorizer, IDF, Normalizer}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.hive.HiveContext

case class Accuracy(
                     precision:Double
                   )

object CashPredict {

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("CashPredict")
    conf.set("spark.yarn.executor.memoryOverhead","4096")
    //设置成spark application总cpu core数量的2~3倍
    conf.set("spark.default.parallelism", "50")
    val sc = new SparkContext(conf)
    val hiveContext = new HiveContext(sc)

    import hiveContext.implicits._

    Class.forName("oracle.jdbc.driver.OracleDriver")
    val url = "jdbc:oracle:thin:@192.168.0.194:1521:smsdb"
    val driver = "oracle.jdbc.driver.OracleDriver"
    val properties = new Properties()
    properties.put("user", "smsdb")
    properties.put("password", "chuanglan789")
    properties.put("driver", driver)
    val oriDF = hiveContext.read.jdbc(url,"loanclassify",properties).select("ID","ACCOUNT","MOBILE","CONTENT","CREATE_TIME","TYPE")

    //val oriDF = hiveContext.table("zhiqun.loanclassify").select("ID","ACCOUNT","MOBILE","CONTENT","CREATE_TIME","TYPE")
    val contentDF=oriDF.rdd
      .filter(i=>i(3).toString.replaceAll("\\s","").length>1)
      //注意表的字段应都为字符串类型，不能是int类型
      //.map(i=>(i(0).toString,i(1).toString,i(2).toString,new ChineseSplit().runChineseAnnotators(i(3).toString,Stopwords_zh.stopWords),i(4).toString,i(5).toString))
        .map(i=>(i(0).toString,i(1).toString,i(2).toString,i(3).toString,new ChineseSplit().runAnsjSplit(i(3).toString),i(4).toString,i(5).toString))   //0.9440922190201729
        //.map(i=>(i(0).toString,i(1).toString,i(2).toString,i(3).toString,new ChineseSplit().runHanLPSplit(i(3).toString),i(4).toString,i(5).toString))
      //.map(i=>(i(0).toString,i(1).toString,i(2).toString,new ChineseSplit().runIKAnalyzerSplit(i(3).toString),i(4).toString,i(5).toString))
      // .map(i=>(i(0).toString,i(1).toString,i(2).toString,i(3).toString,new ChineseSplit().runJiebaSplit(i(3).toString),i(4).toString,i(5).toString))   //0.9487031700288184
     // .map(i=>(i(0).toString,i(1).toString,i(2).toString,new ChineseSplit().runWordSplit(i(3).toString),i(4).toString,i(5).toString))
      .toDF("ID","ACCOUNT","MOBILE","CONTENT","CONTENT_AN","CREATE_TIME","TYPE")


    //注意标签列必须是double类型
    val castDF=contentDF.withColumn("TYPE",contentDF("TYPE").cast("double"))

    val cvModel=new CountVectorizer()
      .setInputCol("CONTENT_AN")
      .setOutputCol("rawFeatures")
      .setMinDF(5)
    val idfModel = new IDF()
      .setInputCol("rawFeatures")
      .setOutputCol("features")
    val normalizer = new Normalizer()
      .setInputCol("features")
      .setOutputCol("normalizedFeatures")
      .setP(2)

    val naiveBayes = new NaiveBayes()
      .setFeaturesCol("normalizedFeatures")
      .setLabelCol("TYPE")
      .setPredictionCol("prediction")
      .setModelType("multinomial")
      .setSmoothing(1.0)

    //val Array(trainingData, testData) = castDF.randomSplit(Array(0.8, 0.2), seed = 1234L)

    val trainingData=castDF
    //注意testDF必须转换为算法输入格式的字段
    val testData=hiveContext
      .table("sd.mt_msg_bigdata").where("opt_mon='201709'").where("ACCOUNT_TYPE=1").where("REPORT='DELIVRD'").select("ID","ACCOUNT","MOBILE","CONTENT","CREATE_TIME")
        //.read.jdbc(url,s"${args(0)}",properties)
      .map(i=>(i(0).toString,i(1).toString,i(2).toString,i(3).toString,new ChineseSplit().runAnsjSplit(i(3).toString),i(4).toString))
      .toDF("ID","ACCOUNT","MOBILE","CONTENT","CONTENT_AN","CREATE_TIME")

    val pipeline = new Pipeline().setStages(Array(cvModel,idfModel,normalizer,naiveBayes))
    val model=pipeline.fit(trainingData)
    val testDF=model.transform(testData)
      .where("prediction=1").select("ID","ACCOUNT","MOBILE","CONTENT","CREATE_TIME")

    //ID| ACCOUNT|MOBILE|CONTENT|CONTENT_AN|CREATE_TIME|TYPE|rawFeatures|features|normalizedFeatures|rawPrediction|probability|prediction|

    //testDF.show(false)
    testDF.registerTempTable("p")
    //不能使用overwrite，ERROR:parquet.schema.InvalidSchemaException: Cannot write a schema with an empty group: message spark_schema
    hiveContext.sql("insert into zhiqun.loan_test09_pre select * from p").write.mode("append").saveAsTable("zhiqun.loan_test09_pre")
    //testDF.write.mode("overwrite").jdbc(url,"loan_01pre",properties)


/*    val evaluator=new MulticlassClassificationEvaluator()
      .setLabelCol("TYPE")
      .setPredictionCol("prediction")
      .setMetricName("precision")
    val predictionAccuracy=evaluator.evaluate(testDF)
    val pd=sc.parallelize(Seq(predictionAccuracy)).map(i=>Accuracy(i)).toDF("PRECISION")
    //println(predictionAccuracy)
    pd.registerTempTable("t")
    hiveContext.sql("insert into zhiqun.PRECISION select * from t").write.mode("append").saveAsTable("zhiqun.PRECISION")*/

    val trueDF=hiveContext.table("wzq.finance_content").where("date='201709'").select("ID","ACCOUNT","MOBILE","FINANCE_CONTENT","CREATE_TIME")
      .toDF("ID","ACCOUNT","MOBILE","CONTENT","CREATE_TIME")

    //accuracy
    val trueCount=trueDF.intersect(testDF).count()
    //count是Long类型，注意转换为double再相除,否则结果为0
    val accuracy=trueCount.toDouble/trueDF.count().toDouble
    val pd=sc.parallelize(Seq(accuracy)).map(i=>Accuracy(i)).toDF("PRECISION")
    pd.registerTempTable("t")
    hiveContext.sql("insert into zhiqun.PRECISION select * from t").write.mode("append").saveAsTable("zhiqun.PRECISION")

    /**
      * 9月预测率：
      * ansj:
      * hanlp:
      * ik:
      * jieba:
      * word:
      */

  }
}
