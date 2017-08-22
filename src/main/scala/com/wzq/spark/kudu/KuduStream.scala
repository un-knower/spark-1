package com.wzq.spark.kudu

import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kudu.spark.kudu._
import org.apache.kudu.client._
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types._
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import collection.JavaConverters._

case class KuduStream(id:Int,age:Int,name:String)
object KuduStream {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("KuduStream").setMaster("local[2]")
    val sc = new SparkContext(conf)
    val ss = SparkSession.builder().getOrCreate()
    val ssc = new StreamingContext(sc, Seconds(2))

    //kafka params
    val kafkaParams = Map(
      "bootstrap.servers" -> "192.168.94.7:9092",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "kuduStream",
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (false: java.lang.Boolean))
    val topics = Array("kuduStream")

    //kudu params
    val kuduMaster="192.168.94.7:7051"
    @transient
    val kuduContext=new KuduContext(kuduMaster,sc)
    val kuduTableName = "people"
    val kuduTableSchema=StructType(
      StructField("id", IntegerType , false)::StructField("age" , IntegerType, true )::StructField("name",StringType, true )::Nil
    )
    val kuduPrimaryKey = Seq("id")
    val kuduTableOptions =new CreateTableOptions()
    val kuduOptions:Map[String,String]=Map(
      "kudu.table"->kuduTableName,
      "kudu.master" -> kuduMaster
    )
    kuduTableOptions.setRangePartitionColumns(List("id").asJava).setNumReplicas(1)
    if(kuduContext.tableExists(kuduTableName)){
      kuduContext.deleteTable(kuduTableName)
    }
    kuduContext.createTable(kuduTableName,kuduTableSchema,kuduPrimaryKey,kuduTableOptions)

    //kafka streaming
    val messages = KafkaUtils.createDirectStream(ssc, PreferConsistent, Subscribe[String, String](topics, kafkaParams))
    val stream=messages.map(_.value()).transform(rdd=>{
      val parse=ss.read.json(rdd)
      parse.rdd
    })
    import ss.implicits._
    stream.foreachRDD(rdd=>{
          val jsonDf=rdd.map(a=>KuduStream(a(0).toString.toInt,a(1).toString.toInt,a(2).toString)).toDF()
          kuduContext.insertIgnoreRows(jsonDf,kuduTableName)
        })
    ssc.start()
    ssc.awaitTermination()

  }
}
