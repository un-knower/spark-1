package clworks.hive2es

import org.elasticsearch.spark.sql._


object Hive2ES {
  def main(args: Array[String]): Unit = {
    import EsConfs.sqlContext.implicits._

    val oriDF=EsConfs.hiveContext.read.table("zhiqun.A3")
    oriDF.saveToEs("hive/test")
  }
}
