package com.wzq.spark.streaming

import org.apache.spark.sql.SparkSession
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.streaming.{Seconds, State, StreamingContext}

object SocketStream {
  def main(args: Array[String]): Unit = {

    //基本设置
    val conf = new SparkConf().setAppName("stream").setMaster("local[2]")
    val sc = new SparkContext(conf)
    val ss = SparkSession.builder().config(conf).enableHiveSupport().getOrCreate()
    val checkpointDirectory = "checkPoint"
    //创建StreamingContext
    def functionToCreateContext(): StreamingContext = {
      val ssc = new StreamingContext(sc, Seconds(2))
      ssc.checkpoint(checkpointDirectory)
      ssc
    }
    //以防同时产生StreamingContext
    val context = StreamingContext.getOrCreate(checkpointDirectory, () => functionToCreateContext())
    //更新统计

/*    val mappingFunc = (word: String, count: Option[Int], state: State[Int]) => {
      val sum = count.getOrElse(0) + state.getOption.getOrElse(0)
      val output = (word, sum)
      state.update(sum)
      output
    }*/

    val wordCounts = context.socketTextStream("192.168.94.2", 9999).flatMap(_.split(" ")).map((_, 1)).updateStateByKey(
      (newValues: Seq[Int], runningCount: Option[Int]) => {
        val currentSum = newValues.sum
        val previousSum = runningCount.getOrElse(0)
        Some(currentSum + previousSum)
      }
    )
    import ss.implicits._
    wordCounts.foreachRDD(
      rdd => {
        rdd.toDF().write.mode("overwrite").saveAsTable("socketStream")
      }
    )
    context.start()
    context.awaitTermination()
  }

}
