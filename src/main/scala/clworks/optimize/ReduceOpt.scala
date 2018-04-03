package clworks.optimize

import java.util.Properties

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.hive.HiveContext

object ReduceOpt {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("reduce").setMaster("local[3]")
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

    /**
      * reducebykey的求和、求平均，最大值
      */
    val df1=hiveContext.read.jdbc(url,"FREQ",properties)

    //val df2=df1.map(i=>(i(0).toString,i(1).toString)).reduceByKey((i,j)=>(i.toInt+j.toInt).toString).toDF()
    //val df2=df1.map(i=>(i(0).toString,i(1).toString)).reduceByKey((i,j)=>Math.max(i.toInt,j.toInt).toString).toDF()
    //val df2=df1.groupBy("ACCOUNT").agg(Map("TIME"->"avg"))

/*    df1.map(i=>(i(0).toString,i(1).toString.toInt)).combineByKey(
      time=>(time,1),
      (i:(Int,Int),j:Int)=>(i._1+j,i._2+1),
      (i:(Int,Int),j:(Int,Int))=>(i._1+j._1,i._2+j._2)
    ).map{
      case (k,v)=>(k,v._1.toDouble/v._2.toDouble)
    }
      .toDF()
    .show()*/
  }
}
