import org.apache.spark.sql.SparkSession

object Chuanglan_Partition {
  def main(args: Array[String]): Unit = {
    val ss=SparkSession.builder().appName("partition").master("local[3]")
      .getOrCreate()
    import ss.implicits._
    val fenquDF=ss.read.option("header","true").csv("data/linke_account_20171122_2_ac.csv")
      .repartition(10)
    fenquDF.write.csv("C:\\Users\\ChuangLan\\Desktop\\分区")

   // println(fenquDF.count())

  }
}
