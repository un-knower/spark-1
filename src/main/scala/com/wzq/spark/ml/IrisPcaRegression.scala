package com.wzq.spark.ml

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.classification.LogisticRegression
import org.apache.spark.ml.evaluation.{BinaryClassificationEvaluator, MulticlassClassificationEvaluator}
import org.apache.spark.ml.feature.{PCA, StringIndexer, VectorAssembler}
import org.apache.spark.ml.linalg.Vectors
import org.apache.spark.ml.regression.LinearRegression
import org.apache.spark.ml.tuning.{CrossValidator, ParamGridBuilder}
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.{DoubleType, StructField, StructType}

/**
  * Created by wangzhiqun on 2017/8/15.
  */

/**
  * 1.主成分分析
  * 通过正交转换将原始数据变换为一组各维度线性无关的表示，可用于提取数据的主要特征分量，常用于高维数据的降维
  * PCA降维前必须对原始数据（特征向量）进行标准化处理，StandardScaler
  * 2.逻辑回归
  * 对上一步的结果主成分再进行分析
  */

object IrisPcaRegression {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("iris").setMaster("local[2]")
    val sc = new SparkContext(conf)
    val ss = SparkSession.builder().getOrCreate()
    //加载resources下资源
    //val path=this.getClass.getClassLoader.getResource("iris.csv").getPath
    val data=ss.read.options(Map("header"->"true","inferSchema"->"true")).csv("data/iris.csv").toDF()
    val Array(training, test) = data.randomSplit(Array(0.9, 0.1), seed = 1L)
    val indexer=new StringIndexer().setInputCol("Species").setOutputCol("label")
    val features=new VectorAssembler()
      .setInputCols(Array("Sepal_Length",	"Sepal_Width",	"Petal_Length"	,"Petal_Width"))
      .setOutputCol("features")

    /**
      * val pca=new PCA().setK(4)
      * java.util.NoSuchElementException: Failed to find a default value for inputCol
      * 在创建estimation 设置inputCol
      */
    val pca=new PCA().setInputCol(features.getOutputCol).setOutputCol("pcafeatures")
    val logistic=new LogisticRegression().setMaxIter(10).setFeaturesCol(pca.getOutputCol).setLabelCol(indexer.getOutputCol)
    val pipeline=new Pipeline().setStages(Array(indexer,features,pca,logistic))
    val paramGrid=new ParamGridBuilder()
    .addGrid(pca.k,Array(2,3))
     .build()
    val cv=new CrossValidator()
      .setEstimator(pipeline)
      .setEvaluator(new MulticlassClassificationEvaluator())
      .setEstimatorParamMaps(paramGrid)
     .setNumFolds(2)
    val model=cv.fit(training)
    
    //println(model.explainParams())
    //println(model.getEvaluator.isLargerBetter)     true   评估的度量值是大的好，还是小的好
    //model.transform(test).selectExpr("prediction","round(prediction,1) as newPrediction").show(false)
    model.transform(test).show(false)

    /**
      1.explainParams：
estimator: estimator for selection (current: pipeline_bc87d0273c1e)
estimatorParamMaps: param maps for the estimator (current: [Lorg.apache.spark.ml.param.ParamMap;@2182ebc7)
evaluator: evaluator used to select hyper-parameters that maximize the validated metric (current: mcEval_9e4a7c6e4dff)
numFolds: number of folds for cross validation (>= 2) (default: 3, current: 2)
seed: random seed (default: -1191137437)

    2.printSchema
root
 |-- Sepal_Length: double (nullable = true)
 |-- Sepal_Width: double (nullable = true)
 |-- Petal_Length: double (nullable = true)
 |-- Petal_Width: double (nullable = true)
 |-- Species: string (nullable = true)
 |-- label: double (nullable = true)
 |-- features: vector (nullable = true)
 |-- pcafeatures: vector (nullable = true)
 |-- prediction: double (nullable = true)


      */
  }
}
