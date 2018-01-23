import java.util.Properties

import org.apache.spark.sql.SparkSession

object Json2orc {
  def main(args: Array[String]): Unit = {
    val ss=SparkSession.builder().appName("test").master("local[3]")
      .getOrCreate()
    import ss.implicits._

    Class.forName("oracle.jdbc.driver.OracleDriver")
    val url="jdbc:oracle:thin:@192.168.0.194:1521:smsdb"
    val driver="oracle.jdbc.driver.OracleDriver"
    val properties=new Properties()
    properties.put("user","smsdb")
    properties.put("password","chuanglan789")
    properties.put("driver",driver)

    //spark的json字段按照字母顺序排序
    val jfile=ss.read.json("D:\\webmagic-retail\\www.qichacha.com").toDF("COMPANY_NAME","EMAIL","MOBILE")

    //填充null为0
    //jfile.na.fill("0").show(50)

    //jfile.show(100)

//    jfile.map(i=>(i(0).toString.substring(i(0).toString.indexOf("[")+1,i(0).toString.indexOf("]")),i(1).toString.substring(i(1).toString.indexOf("[")+1,i(1).toString.indexOf("]")),i(2).toString.substring(i(2).toString.indexOf("[")+1,i(2).toString.indexOf("]"))))
//        .show()


/*    jfile.map(i=>(Option(i(0).toString.substring(i(0).toString.indexOf("[")+1,i(0).toString.indexOf("]"))).getOrElse("0"),
      Option((1).toString.substring(i(1).toString.indexOf("[")+1,i(1).toString.indexOf("]"))).getOrElse("0"),
      Option(i(2).toString.substring(i(2).toString.indexOf("[")+1,i(2).toString.indexOf("]"))).getOrElse("0")))
        .show(false)*/

    //jfile.map(i=>(i(0).toString.substring(i(0).toString.indexOf("[")+1,i(0).toString.indexOf("]")),i(1).toString.substring(i(1).toString.indexOf("[")+1,i(1).toString.indexOf("]")),i(2).toString.substring(i(2).toString.indexOf("[")+1,i(2).toString.indexOf("]"))))
      //.show()

     // .write.mode("append").jdbc(url,"Qichacha_MAGAGER_Crawl",properties)


    //jfile.createTempView("p")
    //ss.sql("select * from p where COMPANY_NAME IS  NULL").show(false)

    jfile.write.mode("append").jdbc(url,"Qichacha_MANAGER_Crawl",properties)

  }
}
