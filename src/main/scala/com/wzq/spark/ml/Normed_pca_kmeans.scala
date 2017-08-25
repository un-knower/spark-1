package com.wzq.spark.ml

import com.wzq.spark.utils.Accounts_toDestiny
import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.clustering.KMeans
import org.apache.spark.ml.feature._

object Normed_pca_kmeans {
  def main(args: Array[String]): Unit = {
    val file="hdfs://192.168.94.7:9000/user/zhiqun/170806_67/*"
    val filter="金币消费结算统计"
    val r ="""^(\S+) Bill\[\d+\]  INFO: \[金币消费结算统计\]时间\((\d+)\)类别\((\d+)\)名称\((.*?)\)频道\((\d+)\)等级\((\d+)\)用户\((\d+)\)赠送\((\d+)\)个道具\((\d+)\)给歌手\((\d+)\),歌手等级\((\d+)\),签约\((\d+)\), 消耗金币\((\d+)\), 歌手获得金币\((\d+)\), 频道获得金币\((\d+)\),歌手当前金币\((\d+)\)频道当前金币\((\d+)\)用户当前金币\((\d+)\)""".r
    val dataset=Accounts_toDestiny.hdfsDataSet(file,filter,r)
    val indexer1=new StringIndexer().setInputCol("date").setOutputCol("date_idx")
    val indexer2=new StringIndexer().setInputCol("name").setOutputCol("name_idx")
    val features=new VectorAssembler()
      .setInputCols(Array(indexer1.getOutputCol, "time", "category", indexer2.getOutputCol, "channel", "userLevel", "userId", "give", "tools", "singerId", "singerLevel", "contract", "consumeGold", "singer_gainGold", "channel_gainGold", "singer_currGold", "channel_currGold", "user_currGold"))
      .setOutputCol("features")
    val norm=new Normalizer().setInputCol(features.getOutputCol).setOutputCol("NormedFeatures")
    val pca=new PCA().setInputCol(norm.getOutputCol).setOutputCol("pcafeatures").setK(10)
    val kmeans=new KMeans().setSeed(1L).setMaxIter(10).setK(2)
    val pipeline=new Pipeline().setStages(Array(indexer1,indexer2,features,norm,pca,kmeans))
    /*val standard=new StandardScaler().setInputCol(features.getOutputCol).setOutputCol("standardFeatures").setWithMean(true).setWithStd(true)
    val pca=new PCA().setInputCol(standard.getOutputCol).setOutputCol("pcafeatures").setK(10)
    val kmeans=new KMeans().setSeed(1L).setMaxIter(10).setK(2)
    val pipeline=new Pipeline().setStages(Array(indexer1,indexer2,features,standard,pca,kmeans))*/
    val model=pipeline.fit(dataset)
    model.transform(dataset).show(false)
  }
}
