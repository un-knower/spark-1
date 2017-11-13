package regex

import java.util.Properties

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.hive.HiveContext

object RegMatch {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("hive").setMaster("local[3]")
    val sc = new SparkContext(conf)
    val hiveContext = new HiveContext(sc)
    import hiveContext.implicits._

    //hive正则截取：select mobile, regexp_extract(finance_content,'(逾期)([0-9]+)',2) from wzq.finance_black where finance_content regexp '逾期[0-9]+' limit 100;

    Class.forName("oracle.jdbc.driver.OracleDriver")
    val url = "jdbc:oracle:thin:@172.16.20.21:1521:smsdb"
    val table = "MT_MSG_FINANCE_201710"
    val driver = "oracle.jdbc.driver.OracleDriver"
    val properties = new Properties()
    properties.put("user", "smsdb")
    properties.put("password", "chuanglan789")
    properties.put("driver", driver)

    //scala的正则表达式就是提取器，匹配任意模式，从输入中提取出匹配的部分。把每个括号里的匹配都展开到一个模式变量里,需要几个变量就加上几个括号
    val regex=(".*逾期([0-9]+[天|日|个]).*").r
    val oriDF = hiveContext.read.jdbc(url, table, properties).cache()
    val trimDF=oriDF.map(i=>(i(0).toString,i(1).toString,i(2).toString.replaceAll("\\s+",""))).toDF("account","mobile","content")
    trimDF.rdd
      .filter(i=>i(2).toString.matches(".*逾期([0-9]+)[天|日|个].*"))
      .map(i=>{
        i(2).toString match {
          case regex(a)=>(i(0).toString,i(1).toString,a)
        }
      })
      .toDF("account","mobile","days")
      .show(50)

    //匹配多个括号，然后在原有基础上产生多个列
/*    val regex=(".*逾期([0-9]+)(天|日|个月).*").r
    val oriDF=hiveContext.read.jdbc(url,table,properties)
    val trimDF=oriDF.map(i=>(i(0).toString,i(1).toString,i(2).toString.replaceAll("\\s+",""))).toDF("ACCOUNT","MOBILE","FINANCE_CONTENT")
    trimDF.rdd
      .filter(i=>i(2).toString.matches(".*逾期([0-9]+)(天|日|个月).*"))
      .map(i=>{
        i(2).toString match {
          case regex(a,b)=>(i(0).toString,i(1).toString,a,b)
        }
      })
      .toDF("ACCOUNT","MOBILE","DAYS","NUMS")
      .write.jdbc(url,"MT_MSG_FINANCE_2017101",properties)*/
  }
}
