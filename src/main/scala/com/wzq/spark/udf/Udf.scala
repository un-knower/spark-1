package com.wzq.spark.udf

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SparkSession

/**
  * spark sql udf
  */
object Udf {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("udf").setMaster("local[2]")
    val sc = new SparkContext(conf)
    val ss = SparkSession.builder().getOrCreate()
    import ss.implicits._
    val df=Seq("abcde","zxcv","qwe").toDF("name")
    val strsLen=(s:String)=>s.length
    ss.udf.register("lens",strsLen)
    df.createOrReplaceTempView("tmp")
    ss.sql(s"select name,lens(name) from tmp").show()

    /**
    +-----+--------------+
| name|UDF:lens(name)|
+-----+--------------+
|abcde|             5|
| zxcv|             4|
|  qwe|             3|
+-----+--------------+
      */
  }
}
