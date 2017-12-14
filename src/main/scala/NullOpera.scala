import java.util.Properties

import org.apache.spark.sql.SparkSession

object NullOpera {
  def main(args: Array[String]): Unit = {
    val ss=SparkSession.builder().appName("partition").master("local[3]")
      .getOrCreate()
    Class.forName("oracle.jdbc.driver.OracleDriver")
    val url="jdbc:oracle:thin:@192.168.0.194:1521:smsdb"
    val driver="oracle.jdbc.driver.OracleDriver"
    val properties=new Properties()
    properties.put("user","smsdb")
    properties.put("password","chuanglan789")
    properties.put("driver",driver)

    /**
      * 数据库中null的处理
      */
    val df1=ss.read.jdbc(url,"FREQ",properties)
    //删除所有列的空值，值返回不包含null的行
    //df1.na.drop().show()
    //删除某列的null
   // df1.na.drop(Array("ACCOUNT","TIME")).show()
    //填充所有空值的列
    //df1.na.fill("-----").show()
    //对指定列的空值填充
    //df1.na.fill(value="**",cols = Array("ACCOUNT")).show()
    //不同的列填充不同的值
    df1.na.fill(Map("ACCOUNT"->"AAAAAA","TIME"->"BBBBBBBBB")).show()
  }
}
