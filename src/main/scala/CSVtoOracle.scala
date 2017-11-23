import java.util.Properties

import org.apache.spark.sql.SparkSession

object CSVtoOracle {
  def main(args: Array[String]): Unit = {
    val ss=SparkSession.builder().appName("bayes").master("local[3]")
      .getOrCreate()
    import ss.implicits._

    Class.forName("oracle.jdbc.driver.OracleDriver")
    val url="jdbc:oracle:thin:@192.168.0.194:1521:smsdb"
    val table="ACCOUNT_INFO"
    val driver="oracle.jdbc.driver.OracleDriver"
    val properties=new Properties()
    properties.put("user","smsdb")
    properties.put("password","chuanglan789")
    properties.put("driver",driver)


    //sqoop import --hive-import  --connect  jdbc:oracle:thin:@192.168.0.194:1521:smsdb --username smsdb --password chuanglan789
    // --table ACCOUNT_INFO  --hive-database wzq  --hive-table account_info -m 1
    val df1=ss.read.option("header","true").csv("data/ACCOUNT_INFO.txt")
    df1.write.jdbc(url,table,properties)

    //df1.take(10).foreach(println(_))
/*    ss.read.option("header","true").csv("data/mobile_name.csv").createTempView("p")
    ss.sql("insert into tableName select * from p")
      .write.mode("append").saveAsTable("tableName")*/
  }
}
