package clworks.hive2es

import java.util.Properties

object Ora2Hive {
  def main(args: Array[String]): Unit = {

    Class.forName("oracle.jdbc.driver.OracleDriver")
    val url = "jdbc:oracle:thin:@192.168.0.194:1521:smsdb"
    val driver = "oracle.jdbc.driver.OracleDriver"
    val table ="A3"
    val properties = new Properties()
    properties.put("user", "smsdb")
    properties.put("password", "chuanglan789")
    properties.put("driver", driver)

    val oriDF=EsConfs.hiveContext.read.jdbc(url,table,properties)
    val cast=oriDF.withColumn("coun",oriDF("coun").cast("int"))
    cast.write.mode("append").saveAsTable("zhiqun.A3")

  }
}
