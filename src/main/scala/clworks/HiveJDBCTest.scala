package clworks

import java.sql.DriverManager

import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.hive.HiveContext
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable.ArrayBuffer

object HiveJDBCTest {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("hive").setMaster("local[2]")
    val sc = new SparkContext(conf)
    val sQLContext=new SQLContext(sc)
    val hiveContext = new HiveContext(sc)


    //hive通过jdbc连接
    Class.forName("org.apache.hive.jdbc.HiveDriver")
    val conn=DriverManager.getConnection("jdbc:hive2://172.16.20.30:10000/zhiqun", "root", "253.com")
    val stmt = conn.createStatement()
    val rst=stmt.executeQuery("select * from mq ")

    val arrayBuffer=ArrayBuffer[String]()

    while (rst.next()){
      arrayBuffer+=rst.getString(1)
    }
    val arrs=arrayBuffer.toArray
    arrs.foreach(println(_))
  }
}
