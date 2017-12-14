import java.util.Properties

import org.ansj.splitWord.analysis.ToAnalysis
import org.apache.spark.sql.SparkSession

import scala.collection.mutable.ArrayBuffer

object Nature {
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

    def runAnsjSplit(text:String):String={
      val parse=ToAnalysis.parse(text)
      val terms=parse.getTerms
      val arr=new StringBuilder
      for(i<-0 to terms.size()-1){
        val word=terms.get(i).getNatureStr
        arr.append(word)
      }
      arr.toString()
    }

    val df1=ss.read.textFile("data/ci.txt").toDF("MAINWORDS")
    val df2=df1.map(i=>(i(0).toString,runAnsjSplit(i(0).toString)))
      .toDF("MAINWORDS","NATURE")
    df2.write.text("C:\\Users\\ChuangLan\\Desktop\\nature.txt")
  }
}
