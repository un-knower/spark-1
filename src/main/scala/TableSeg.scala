import java.sql.DriverManager

import org.apache.spark.sql.SparkSession

object TableSeg {
  def main(args: Array[String]): Unit = {
    val ss=SparkSession.builder().appName("part").master("local[3]")
      .getOrCreate()
    import ss.implicits._

    Class.forName("org.apache.hive.jdbc.HiveDriver")
    val conn=DriverManager.getConnection("jdbc:hive2://172.16.20.30:10000/zhiqun", "root", "253.com")
    val stmt = conn.createStatement()
    val rst=stmt.executeQuery("select * from mq ")



  }
}
