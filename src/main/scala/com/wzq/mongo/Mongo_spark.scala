package com.wzq.mongo

import org.apache.spark.sql.{DataFrameWriter, SparkSession}
import com.mongodb.spark._
import com.mongodb.spark.sql._
import com.mongodb.spark.config.{ReadConfig, WriteConfig}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.bson.Document

import scala.collection.JavaConverters._


object Mongo_spark {
  def main(args: Array[String]): Unit = {
    val ss=SparkSession.builder().master("local[2]").appName("mongo")
      .config("spark.mongodb.input.uri","mongodb://192.168.94.7:27017/mytest.people")
      .config("spark.mongodb.output.uri","mongodb://192.168.94.7:27017/mytest.people")
      .getOrCreate()

    //1.SparkSession:write and read mongo
    def sessionRW() ={
      val dataset=ss.read.json("data/people")
      MongoSpark.save(dataset)
      val df=MongoSpark.load(ss)
      df.show()
    }

    //2.sc :write and read mongo
    def scRW()={
      val docs = """
                   |{"name": "Bilbo Baggins", "age": 50}
                   |{"name": "Gandalf", "age": 1000}
                   |{"name": "Thorin", "age": 195}
                   |{"name": "Balin", "age": 178}
                   |{"name": "Kíli", "age": 77}
                   |{"name": "Dwalin", "age": 169}
                   |{"name": "Óin", "age": 167}
                   |{"name": "Glóin", "age": 158}
                   |{"name": "Fíli", "age": 82}
                   |{"name": "Bombur"}""".trim.stripMargin.split("[\\r\\n]+").toSeq
      //saving RDD data into MongoDB, the data must be convertible to a BSON document
      //all BSON Types can be represented as a String value ,stored as a Strings in mongodb
      val bson=docs.map(Document.parse(_))
      ss.sparkContext.parallelize(bson).saveToMongoDB()
      ss.sparkContext.loadFromMongoDB().foreach(println(_))
      ss.read.mongo()
      ss.loadFromMongoDB()
    }

    def monStream()={
      val conf = new SparkConf().setAppName("mongo").setMaster("local[2]")
      val sc = new SparkContext(conf)
      val  ss=SparkSession.builder().enableHiveSupport().getOrCreate()
      @transient
      val ssc=new StreamingContext(sc, Seconds(1))
      ssc.checkpoint("mongo")
      val wordcount=ssc.socketTextStream("192.168.94.7",9999).flatMap(_.split(" ")).map(w=>(w,1)).reduceByKey(_+_)
      case class WordCount(word: String, count: Int)
      wordcount.foreachRDD(rdd=>{
        import ss.implicits._
        rdd.map{
          case (a,b)=>WordCount(a,b)
        }.toDF().write.mode("append").mongo(WriteConfig(Map("uri" -> "mongodb://192.168.94.7:27017/mytest.mongostream")))
      })
      ssc.start()
      ssc.awaitTermination()
    }


  }
}
