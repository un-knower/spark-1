import java.io.{File, FileOutputStream, PrintWriter}
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.Properties

import org.apache.spark.sql.SparkSession

import scala.io.Source

case class TEK_SIGN(
ID:String,
PORT:String,
ADD_DATE:String,
PORT_TYPE:String,
COMPLAINT_DATE:String,
ACCOUNT:String,
PERSON:String,
PHONE:String,
PHONE_AREA:String,
PHONE_OPERATOR:String,
SMS_NUMBER:String,
COMPLAINT_TYPE:String,
SIGN:String,
SMS_CONTENT:String,
COMPLAINT_NUMBER:String,
COMPLAINT_RATE:String,
RESULT:String,
SEND_TOTAL:String,
RECORD:String,
COMPLAINT_SOURCE:String,
REMARK:String,
CREATE_DATE:String,
ADDPERSON:String,
STATUS:String,
PERSON_ID:String,
IS_CUT:String
                   )
object CSVtoOracle {
  def main(args: Array[String]): Unit = {
    val ss=SparkSession.builder().appName("bayes").master("local[3]")
      .getOrCreate()
    import ss.implicits._

    Class.forName("oracle.jdbc.driver.OracleDriver")
    val url="jdbc:oracle:thin:@192.168.0.194:1521:smsdb"
    val driver="oracle.jdbc.driver.OracleDriver"
    val properties=new Properties()
    properties.put("user","smsdb")
    properties.put("password","chuanglan789")
    properties.put("driver",driver)


    //sqoop import --hive-import  --connect  jdbc:oracle:thin:@192.168.0.194:1521:smsdb --username smsdb --password chuanglan789
    // --table ACCOUNT_INFO  --hive-database wzq  --hive-table account_info -m 1

/*    val oriCSV=Source.fromFile("data/SMS_AUDIT_TEMPLATE.csv").getLines()
    val regex="\\{\\d+,\\d+\\}"
    val writer = new PrintWriter(new File("data/SMS_AUDIT_TEMPLATE_2.csv"))
    while (oriCSV.hasNext){
      val line=oriCSV.next().replaceAll(regex,"")
      writer.println(line)
    }*/

    val df1=ss.read.option("header","true").csv("data/账号.csv")
    //println(df1.count())
    //df1.show(100,false)
    df1.write.mode("append").jdbc(url,"AAAA",properties)
    //df1.show(50,false)
    //df1.take(10).foreach(println(_))
/*    ss.read.option("header","true").csv("data/mobile_name.csv").createTempView("p")
    ss.sql("insert into tableName select * from p")
      .write.mode("append").saveAsTable("tableName")*/

    //val df2=ss.read.text("data/mobile.txt").toDF("MOBILE")
    //df2.write.jdbc(url,"mobile_1129",properties)

    //val df1=ss.read.option("header","true").csv("data/mobile_20171130.csv")
     // .toDF("REALNAME","USER_NAME","ID_NUMBER","USER_SEX","USER_AGE","QQ","PRESENT_ADDRESS","PRESENT_ADDRESS_DISTINCT","COMPANY_NAME","COMPANY_ADDRESS","TG_FLAG")
    //println(df1.count())
/*    val df1=ss.read.options(Map(
       "header"->"true",
       "sep"->"|"
     )).csv("data/tek_sign_complaint.txt")
      .toDF("ID","PORT","ADD_DATE","PORT_TYPE","COMPLAINT_DATE","ACCOUNT","PERSON","PHONE","PHONE_AREA",
      "PHONE_OPERATOR","SMS_NUMBER","COMPLAINT_TYPE","SIGN","SMS_CONTENT","COMPLAINT_NUMBER","COMPLAINT_RATE",
      "RESULT","SEND_TOTAL","RECORD","COMPLAINT_SOURCE","REMARK","CREATE_DATE","ADDPERSON","STATUS","PERSON_ID",
        "IS_CUT","COMPLAINT","MONTH","YEAR")
   // println(df1.count())
   df1.write.mode("append").jdbc(url,"TEK_SIGN",properties)*/
    //df1.show(false)

/*    val df1=ss.read.text("data/gongsi.txt").toDF("NAME")
    df1.write.mode("append").jdbc(url,"COMPANY_NAME",properties)*/

/*    def castDate(str:String):String={
      val sdf=new SimpleDateFormat("yyyyMMdd")
      val date=new Date(str.toLong*1000)
      sdf.format(date)
    }

    val df1=ss.read.options(Map(
      "header"->"true"
    )).csv("data/tek_sign_complaint_1.csv")
      .toDF("ID","PORT","ADD_DATE","PORT_TYPE","COMPLAINT_DATE","ACCOUNT","PERSON","PHONE","PHONE_AREA",
        "PHONE_OPERATOR","SMS_NUMBER","COMPLAINT_TYPE","SIGN","SMS_CONTENT","COMPLAINT_NUMBER","COMPLAINT_RATE",
        "RESULT","SEND_TOTAL","RECORD","COMPLAINT_SOURCE","REMARK","CREATE_DATE","ADDPERSON","STATUS","PERSON_ID",
        "IS_CUT")*/
/*    val df2=df1.withColumn("ADD_DATE",df1("ADD_DATE").cast("date"))
    val df3=df2.withColumn("COMPLAINT_DATE",df2("COMPLAINT_DATE").cast("timestamp"))
    val df4=df3.withColumn("CREATE_DATE",df3("CREATE_DATE").cast("timestamp"))
    df2.show()*/
/*    val df2=df1
      .map(i=>TEK_SIGN(i(1).toString,i(2).toString,castDate(i(3).toString),i(4).toString,castDate(i(5).toString),i(6).toString,i(7).toString,
      i(8).toString,i(9).toString,i(10).toString,i(11).toString,i(12).toString,i(13).toString,i(14).toString,i(15).toString,i(16).toString,
      i(17).toString,i(18).toString,i(19).toString,i(20).toString,i(21).toString,castDate(i(22).toString),i(23).toString,i(24).toString,i(25).toString,i(26).toString))
      .toDF("ID","PORT","ADD_DATE","PORT_TYPE","COMPLAINT_DATE","ACCOUNT","PERSON","PHONE","PHONE_AREA",
        "PHONE_OPERATOR","SMS_NUMBER","COMPLAINT_TYPE","SIGN","SMS_CONTENT","COMPLAINT_NUMBER","COMPLAINT_RATE",
        "RESULT","SEND_TOTAL","RECORD","COMPLAINT_SOURCE","REMARK","CREATE_DATE","ADDPERSON","STATUS","PERSON_ID",
        "IS_CUT")
    //df1.write.mode("append").jdbc(url,"TEK_SIGN",properties)
    df2.show()*/
  }
}
