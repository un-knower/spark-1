package clworks

import java.util.zip.ZipInputStream

import org.apache.hadoop.io.{LongWritable, Text}
import org.apache.hadoop.mapred.TextInputFormat
import org.apache.spark.input.PortableDataStream
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.hive.HiveContext

import scala.io.Source

object HH {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("df").setMaster("local[3]")
    conf.set("spark.yarn.executor.memoryOverhead","4096")
    conf.set("spark.default.parallelism","50")
    val sc = new SparkContext(conf)

    sc.binaryFiles("data/00_http.zip")                             //make an RDD from *.zip files
      .flatMap((file: (String, PortableDataStream)) => {                  //flatmap to unzip each file
       val zipStream = new ZipInputStream(file._2.open)                //open a java.util.zip.ZipInputStream
    val entry = zipStream.getNextEntry                              //get the first entry in the stream
    val iter = Source.fromInputStream(zipStream).getLines           //place entry lines into an iterator
      iter.next                                                       //pop off the iterator's first line
      iter                                                            //return the iterator
    })
        .foreach(println(_))
      //.saveAsTextFile("hdfs://172.16.20.30:8020/user/zhiqun/h2.txt")

      //.wholeTextFiles("data/00_http.zip")
     // .hadoopFile("data/00_http.zip", classOf[TextInputFormat], classOf[LongWritable], classOf[Text]).map(pair => new String(pair._2.getBytes, 0, pair._2.getLength, "UTF-8"))
    //.textFile("data/00_http.zip")
      //.map(i=>new String(i.getBytes(),"UTF-8"))
    //file.foreach(println(_))

  }
}
