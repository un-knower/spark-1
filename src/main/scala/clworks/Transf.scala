package clworks

import java.util.Properties

import org.apache.spark.ml.UnaryTransformer
import org.apache.spark.ml.param._
import org.apache.spark.ml.util._
import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.types.{DataType, StringType}
import org.apache.spark.{SparkConf, SparkContext}

class Sub(override val uid:String)
  extends UnaryTransformer[String, String, Sub] {

  def this() = this(Identifiable.randomUID("Sub"))

  override protected def createTransformFunc: (String) => String= {
    s=>
    try{
      s.substring(s.indexOf("【")+1,s.indexOf("】"))
    }catch {
      case e:Exception=>e.toString
    }
  }

  override protected def validateInputType(inputType: DataType): Unit = {
    require(inputType == StringType, s"Input type must be string type but got $inputType.")
  }

  override protected def outputDataType: DataType = StringType

  override def copy(extra: ParamMap): Sub = defaultCopy(extra)
}


case class Transf(
                   id:String,
                   account:String,
                   mobile:String,
                   content:String,
                   create_time:String
                 )

object Transf {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("finance").setMaster("local[3]")
    val sc = new SparkContext(conf)
    val sQLContext = new SQLContext(sc)
    import sQLContext.implicits._

    Class.forName("oracle.jdbc.driver.OracleDriver")
    val url = "jdbc:oracle:thin:@172.16.20.21:1521:smsdb"
    val table = "MT_MSG_20170101"
    val driver = "oracle.jdbc.driver.OracleDriver"
    val properties = new Properties()
    properties.put("user", "smsdb")
    properties.put("password", "chuanglan789")
    properties.put("driver", driver)

    val oriDF = sQLContext.read.jdbc(url, table, properties).select("ID","ACCOUNT","MOBILE","CONTENT","CREATE_TIME")
    val filterDF=oriDF.filter(oriDF("CONTENT").contains("【").and(oriDF("CONTENT").contains("】")))
      .rdd
      .filter(i=>i(3).toString.indexOf("】")>i(3).toString.indexOf("【"))
      .map(i=>Transf(i(0).toString,i(1).toString,i(2).toString,i(3).toString,i(4).toString))
      .toDF("ID","ACCOUNT","MOBILE","CONTENT","CREATE_TIME")

    //添加新列
    val subDF=new Sub()
      .setInputCol("CONTENT")
      .setOutputCol("CONTENT_NEW")

    subDF.transform(filterDF).show(100,false)
  }
}
