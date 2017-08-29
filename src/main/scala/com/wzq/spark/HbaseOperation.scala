package com.wzq.spark

import org.apache.hadoop.hbase.client.Put
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.mapred.TableOutputFormat
import org.apache.hadoop.hbase.mapreduce.TableInputFormat
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.hbase.{HBaseConfiguration, HConstants}
import org.apache.hadoop.mapred.JobConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.{SparkConf, SparkContext}

/**
  * hbase-spark
  */
object HbaseOperation {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("hbase").setMaster("local[2]")
    val sc = new SparkContext(conf)
    val ss=SparkSession.builder().getOrCreate()
    val tableName="user"
    val hconfig=HBaseConfiguration.create()
    hconfig.set(HConstants.ZOOKEEPER_QUORUM,"192.168.94.7:2181")

    //读取hbase表

/*    hconfig.set(TableInputFormat.INPUT_TABLE, tableName)
      val hBaseRDD = sc.newAPIHadoopRDD(hconfig, classOf[TableInputFormat],
      classOf[org.apache.hadoop.hbase.io.ImmutableBytesWritable],
      classOf[org.apache.hadoop.hbase.client.Result])
  //  println(hBaseRDD.count())
    hBaseRDD.foreach{
      case (_,result)=>
        val rowkey=Bytes.toString(result.getRow)
        val name=Bytes.toString(result.getValue(Bytes.toBytes("info1"),Bytes.toBytes("name")))
        println("Row key:"+rowkey +" Name:"+name)
    }*/

    //写入hbase
    val jobConf = new JobConf(hconfig,this.getClass)
    jobConf.setOutputFormat(classOf[TableOutputFormat])
    jobConf.set(TableOutputFormat.OUTPUT_TABLE,tableName)
    def convert(triple: (String, Int)) = {
      val p = new Put(Bytes.toBytes(triple._1))
      p.addColumn(Bytes.toBytes("info1"),Bytes.toBytes("age"),Bytes.toBytes(triple._2))
      (new ImmutableBytesWritable, p)
    }
    //将RDD[(uid:Int, name:String, age:Int)] 转换成 RDD[(ImmutableBytesWritable, Put)]
    val rawData = List(("id1",18))
    val localData = sc.parallelize(rawData).map(convert)
    localData.saveAsHadoopDataset(jobConf)
  }
}
