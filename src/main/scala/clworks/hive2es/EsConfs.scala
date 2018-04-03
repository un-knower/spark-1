package clworks.hive2es

import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.hive.HiveContext
import org.apache.spark.{SparkConf, SparkContext}

object EsConfs {
  val conf = new SparkConf().setAppName("es").setMaster("local[3]")
  conf.set("es.index.auto.create", "true")
  conf.set("es.nodes", "172.16.20.20,172.16.20.22,172.16.20.24")
  conf.set("es.port", "9200")
  //conf.set("es.spark.dataframe.write.null", "true")
  conf.set("spark.yarn.executor.memoryOverhead","4096")
  conf.set("spark.default.parallelism","50")
  val sc = new SparkContext(conf)
  val hiveContext = new HiveContext(sc)
  val sqlContext=new SQLContext(sc)
}
