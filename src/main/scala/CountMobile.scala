import java.util.Properties

import org.apache.spark.sql.SparkSession

object CountMobile {
  def main(args: Array[String]): Unit = {

    Class.forName("oracle.jdbc.driver.OracleDriver")
    val url="jdbc:oracle:thin:@192.168.0.194:1521:smsdb"
    val driver="oracle.jdbc.driver.OracleDriver"
    val properties=new Properties()
    properties.put("user","smsdb")
    properties.put("password","chuanglan789")
    properties.put("driver",driver)

    val ss=SparkSession.builder().appName("mc").master("local[3]")
      .getOrCreate()
    val reg="1\\d{10}"


    val mobile_all=ss.read.textFile("data/lingke_20180316").filter(_.matches(reg)).toDF("MOBILE")
    mobile_all.write.jdbc(url,"MOBILE_316_A",properties)


    //28647743
    //println(mobile_all.count())
    import ss.implicits._
    val mobile_count=mobile_all.groupBy("MOBILE").count().sort($"count".desc).toDF("MOBILE","COUNTS")
    //mobile_count.show()
    //mobile_count.write.jdbc(url,"MOBILE_316",properties)

/*    val ss=SparkSession.builder().appName("mc").master("local[3]")
      .getOrCreate()
    import ss.implicits._
    val mobile_all=ss.read.textFile("data/tt").toDF("mobile")
    mobile_all.groupBy("mobile").count().sort($"count".desc).show()*/



  }
}
