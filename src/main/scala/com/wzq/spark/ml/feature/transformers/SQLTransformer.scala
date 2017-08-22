package com.wzq.spark.ml.feature.transformers

import org.apache.spark.ml.feature.SQLTransformer
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SparkSession

/**
  * 目前只支持 "SELECT ... FROM __THIS__ ..."
  */
object SQLTransformer {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("mlib").setMaster("local[2]")
    val sc = new SparkContext(conf)
    val ss = SparkSession.builder().getOrCreate()
    val df = ss.createDataFrame(
      Seq((0, 1.0, 3.0), (2, 2.0, 5.0))).toDF("id", "v1", "v2")
    val sqlTrans=new SQLTransformer().setStatement(
      "SELECT *, (v1 + v2) AS v3, (v1 * v2) AS v4 FROM __THIS__"
    )
    sqlTrans.transform(df).show(false)

    /**
      |id |v1 |v2 |v3 |v4  |
+---+---+---+---+----+
|0  |1.0|3.0|4.0|3.0 |
|2  |2.0|5.0|7.0|10.0|
+---+---+---+---+----+
      */
  }
}
