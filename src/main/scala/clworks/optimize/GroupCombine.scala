package clworks.optimize

import java.util.Properties

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.hive.HiveContext

object GroupCombine {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("temp").setMaster("local[3]")
    conf.set("spark.yarn.executor.memoryOverhead","4096")
    val sc = new SparkContext(conf)
    val hiveContext = new HiveContext(sc)
    import hiveContext.implicits._

    Class.forName("oracle.jdbc.driver.OracleDriver")
    val url="jdbc:oracle:thin:@192.168.0.194:1521:smsdb"
    val user="smsdb"
    val password="chuanglan789"
    val driver="oracle.jdbc.driver.OracleDriver"
    val properties=new Properties()
    properties.put("user",user)
    properties.put("password",password)
    properties.put("driver",driver)

    // groupby、combinebyKey 和 reduceByKey
    val oriDF=hiveContext.read.jdbc(url,"FREQ",properties)

    //spark的count操作将null当作0
    //oriDF.groupBy("ACCOUNT").agg(Map("TIME"->"sum")).show()

    //注意reduce操作是Int类型，如果是String类型则是字符串拼接
    //oriDF.map(i=>(i(0).toString,i(1).toString)).reduceByKey((i,j)=>(i.toInt+j.toInt).toString).toDF().show()

    //自定义类型
    type MVType = (Int, Double)
/*
    oriDF.map(i=>(i(0).toString,i(1).toString.toInt))
      //.combineByKey(x=>x,(x:Int,y:Int)=>x+y,(x:Int,y:Int)=>x+y).toDF()  //求和

    /**
      * 求平均数
      * 1.createCombiner: time为当前原始数据，1为计数器
      * 2.mergeValue: i为计数器和原始数据的元组，j为下一个数据，在同一分区内操作（计数器加1，数据相加）
      * 3.mergeCombiners：所有分区进行合并
      */

      .combineByKey(
      time=>(1,time),
      (i:(Int,Int),j)=>(i._1+1,i._2+j),
      (i:(Int,Int),j:(Int,Int))=>(i._1+j._1,i._2+j._2)
    )
        .map{
          case (account,(num,score))=>(account,score/num)
        }
      .toDF()
      .show()*/
  }
}
