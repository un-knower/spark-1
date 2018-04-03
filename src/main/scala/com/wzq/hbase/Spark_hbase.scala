package com.wzq.hbase

import java.util.Properties

import org.apache.hadoop.hbase.client.Put
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.mapred.TableOutputFormat
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.hbase.{HBaseConfiguration, HConstants}
import org.apache.hadoop.mapred.JobConf
import org.apache.spark.sql.SQLContext
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable.ArrayBuffer

object Spark_hbase {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("tohbase")
    val sc = new SparkContext(conf)
    val sQLContext=new SQLContext(sc)
    val hconfig=HBaseConfiguration.create()
    hconfig.set(HConstants.ZOOKEEPER_QUORUM,"172.16.20.51,172.16.20.52,172.16.20.53,172.16.20.54,172.16.20.55")
    hconfig.set(HConstants.ZOOKEEPER_CLIENT_PORT,"2181")

    Class.forName("oracle.jdbc.driver.OracleDriver")
    val url="jdbc:oracle:thin:@172.16.20.21:1521:smsdb"
    val driver="oracle.jdbc.driver.OracleDriver"
    val properties=new Properties()
    properties.put("user","smsdb")
    properties.put("password","chuanglan789")
    properties.put("driver",driver)

    //传入参数
/*    val orTable=args(0)
    val jobConf = new JobConf(hconfig,this.getClass)
    jobConf.setOutputFormat(classOf[TableOutputFormat])
    jobConf.set(TableOutputFormat.OUTPUT_TABLE,"MT_MSG_201705_all")
    val df1=sQLContext.read.jdbc(url,orTable,properties).select("MOBILE","ID","ACCOUNT","CONTENT","CREATE_TIME")
    val df2=df1.map{r=>{
      val p = new Put(Bytes.toBytes(r(0).toString.reverse+"-"+r(1).toString))
      p.addColumn(Bytes.toBytes("cf"), Bytes.toBytes("ACCOUNT"), Bytes.toBytes(r(2).toString))
      p.addColumn(Bytes.toBytes("cf"), Bytes.toBytes("CONTENT"), Bytes.toBytes(r(3).toString))
      p.addColumn(Bytes.toBytes("cf"), Bytes.toBytes("CREATE_TIME"), Bytes.toBytes(r(4).toString))
      (new ImmutableBytesWritable, p)
    }}
    df2.saveAsHadoopDataset(jobConf)*/

     val arrayBuffer=ArrayBuffer[String]()
     for (i<-args(0).toInt to args(1).toInt) {
      val orTable = "MT_MSG_" + i
      arrayBuffer+=orTable
    }
    val tables=arrayBuffer.toArray
    for(table<-tables){
      val jobConf = new JobConf(hconfig,this.getClass)
      jobConf.setOutputFormat(classOf[TableOutputFormat])
      jobConf.set(TableOutputFormat.OUTPUT_TABLE,"MT_MSG_FINANCE")
      val originDF=sQLContext.read.jdbc(url,table,properties).select("MOBILE","ID","ACCOUNT","CONTENT","CREATE_TIME")
      val financeDF=originDF.filter(
        originDF("CONTENT").contains("还清")
          .or(originDF("CONTENT").contains("还款成功"))
          .or(originDF("CONTENT").contains("按时还款"))
          .or(originDF("CONTENT").contains("成功还款"))
          .or(originDF("CONTENT").contains("结清"))
          .or(originDF("CONTENT").contains("提额"))
          .or(originDF("CONTENT").contains("正常还款"))
      )
/*      .toDF("ID","ACCOUNT","MOBILE","FINANCE_CONTENT","CREATE_TIME")
      financeDF.map{r=>{
          val p = new Put(Bytes.toBytes(r(0).toString.reverse+"-"+r(1).toString))
          p.addColumn(Bytes.toBytes("cf"), Bytes.toBytes("ACCOUNT"), Bytes.toBytes(r(2).toString))
          p.addColumn(Bytes.toBytes("cf"), Bytes.toBytes("CONTENT"), Bytes.toBytes(r(3).toString))
          p.addColumn(Bytes.toBytes("cf"), Bytes.toBytes("CREATE_TIME"), Bytes.toBytes(r(4).toString))
          (new ImmutableBytesWritable, p)
        }}
        .saveAsHadoopDataset(jobConf)*/
      }
  }
}
