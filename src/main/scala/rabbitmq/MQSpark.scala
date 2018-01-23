package rabbitmq

import org.apache.log4j.{Level, Logger}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.rabbitmq.RabbitMQUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}

object MQSpark {
  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR)

    val conf = new SparkConf().setAppName("MQ").setMaster("local[3]")
    conf.set("spark.yarn.executor.memoryOverhead","4096")
    conf.set("spark.default.parallelism","50")
    val sc = new SparkContext(conf)
    val ssc=new StreamingContext(sc,Seconds(5))


    val mqParams=Map("host"->"192.168.0.40","userName"->"lsrabbit","password"->"lsrabbit","queueName"->"zhiqun_test")
    val stream=RabbitMQUtils.createStream[String](ssc,mqParams)
    stream.foreachRDD{i=>
      println(i.count())
    }
    ssc.start()
    ssc.awaitTermination()
  }
}
