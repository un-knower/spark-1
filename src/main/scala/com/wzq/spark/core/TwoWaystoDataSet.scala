package com.wzq.spark.core

import org.apache.spark.sql.{Row, SparkSession}
import org.apache.spark.sql.types.{StringType, StructField, StructType}

object TwoWaystoDataSet {

  case class People(age:Long,name:String)
  def main(args: Array[String]): Unit = {

    val ss=SparkSession.builder().appName("dataset").master("local[2]").getOrCreate()

    /**
      *     RDDs to DataFrames
      *     1.“case class”的属性对应“列的名字”,可以注册成表
      *     2.通过SparkSession创建sparkContext和sqlcontext
      */

    // For implicit conversions from RDDs to DataFrames
/*    import ss.implicits._

    val peopleDF=ss.sparkContext.textFile("hdfs://192.168.94.7:9000/a.txt").map(_.split(" "))
    .map(attr=>People(attr(0).toInt,attr(1))).toDF()

    peopleDF.createOrReplaceTempView("people")

    val df = ss.sql("SELECT name, age FROM people")

    df.map(col=>"NAME:"+col(0)).show()
    df.map(col=>"name:"+col.getAs("age")).show()*/


    /**
      *   1.从源rdd创建RowRdd
      *   2.创建schema与RowRdd匹配的StructType
      *   3.createDataFrame
      */

    val peopleRDD = ss.sparkContext.textFile("hdfs://192.168.94.7:9000/a.txt")

    val schemaString = "age name"

    val fields=schemaString.split(" ").map(fieldName => StructField(fieldName, StringType, nullable = true))

    val schema = StructType(fields)

    val rowRdd=peopleRDD.map(_.split(" ")).map(attr=>Row(attr(0),attr(1)))

    val peopleDF=ss.createDataFrame(rowRdd,schema)

    peopleDF.createOrReplaceTempView("peo_ple")

    val results = ss.sql("SELECT name FROM peo_ple")

    results.show()
  }
}
