import java.util.Properties

import org.apache.spark.sql.SparkSession

object Chuanglan_Partition {
  def main(args: Array[String]): Unit = {
    val ss=SparkSession.builder().appName("partition").master("local[3]")
      .getOrCreate()
    import ss.implicits._
    val fenquDF=ss.read.option("header","false").csv("data/MEDIUM_10.csv")
      .repartition(3)
    //fenquDF.write.csv("C:\\Users\\ChuangLan\\Desktop\\分区")

   // println(fenquDF.count())


    Class.forName("oracle.jdbc.driver.OracleDriver")
    val url="jdbc:oracle:thin:@192.168.0.194:1521:smsdb"
    val driver="oracle.jdbc.driver.OracleDriver"
    val properties=new Properties()
    properties.put("user","smsdb")
    properties.put("password","chuanglan789")
    properties.put("driver",driver)

    /*val df1=ss.read.jdbc(url,"MO_MSG",properties)
    df1.repartition(10).write.csv("C:\\Users\\ChuangLan\\Desktop\\分区")*/


    val list1=List(("a","b"))
    val list2=List(("c","d"))
    val rdd1=ss.sparkContext.parallelize(list1,1)
    val rdd2=ss.sparkContext.parallelize(list2,1)
    rdd1.zip(rdd2).foreach(println(_))
  }
}
