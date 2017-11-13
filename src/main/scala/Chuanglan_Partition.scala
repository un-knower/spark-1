import org.apache.spark.sql.SparkSession

object Chuanglan_Partition {
  def main(args: Array[String]): Unit = {
    val ss=SparkSession.builder().appName("bayes").master("local[3]")
      .getOrCreate()
    import ss.implicits._
    val fenquDF=ss.read.csv("data/lingke_account_201710.csv")
      .repartition(5)
    fenquDF.write.csv("C:\\Users\\ChuangLan\\Desktop\\分区")

    //println(fenquDF.count())

  }
}
