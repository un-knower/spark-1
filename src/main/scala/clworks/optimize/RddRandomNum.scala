package clworks.optimize

import java.util.Properties

import org.apache.commons.lang3.RandomUtils
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SQLContext

import scala.collection.mutable.ArrayBuffer

object RddRandomNum {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("rdd")
    val sc = new SparkContext(conf)
    val sQLContext=new SQLContext(sc)
    import sQLContext.implicits._

    Class.forName("oracle.jdbc.driver.OracleDriver")
    val url = "jdbc:oracle:thin:@172.16.20.21:1521:smsdb"
    val driver = "oracle.jdbc.driver.OracleDriver"
    val properties = new Properties()
    properties.put("user", "smsdb")
    properties.put("password", "chuanglan789")
    properties.put("driver", driver)

    val df1=sQLContext.read.jdbc(url,"FINANCE_201701",properties)

    /**
      *  rdd添加随机数字
      *  1.将列转换为集合，并去除"[]"
      *  2.将随机数作为一列
      *  3.将列与随机数列拼接（集合拼接）
      */
    val indexBuffer=new ArrayBuffer[Long]()  //保存随机数
    val size=df1.rdd.count()
    for(i<-1L to size){
      val index=RandomUtils.nextLong(0L,i)
      indexBuffer+=index
    }
    val suffix=indexBuffer.toArray

    val randomNumDF=sc.parallelize(suffix).toDF("randomNum")
    val sigdDF=df1.select("FINANCE_SIG")
    val sigList=sigdDF.collectAsList()
    val randomList=randomNumDF.collectAsList()
    val sigNew=""
    val sigAppendRandom=new ArrayBuffer[String]()

    for(i<-0 to sigList.size()-1){
      val s1=sigList.get(i).toString()
      val s2=s1.substring(s1.indexOf("[")+1,s1.indexOf("]"))
      val s3=randomList.get(i).toString()
      val s4=s3.substring(s3.indexOf("[")+1,s3.indexOf("]"))
      val s5=sigNew.concat(s2.concat(s4))
      sigAppendRandom+=s5
    }

    sc.parallelize(sigAppendRandom).toDF("FINANCE_SIG_RAN")
      .write.jdbc(url,"MT_MSG_FINANCE_A",properties)

    //最后去除加上的随机数
    //sc.parallelize(sigAppendRandom).toDF("FINANCE_SIG_RAN").map(i=>i(0).toString.substring(0,i(0).toString.indexOf("_"))).toDF().show()
  }
}
