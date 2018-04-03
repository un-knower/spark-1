package clworks

import java.util.Properties

import breeze.linalg.{SparseVector, norm}
import com.github.fommil.netlib.F2jBLAS
import org.ansj.splitWord.analysis.ToAnalysis
import org.apache.spark.mllib.linalg.{DenseMatrix, DenseVector, Matrix, Vectors}
import org.apache.spark.sql.DataFrame
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.hive.HiveContext

import scala.collection.JavaConversions._
import scala.collection.mutable.ArrayBuffer

object Utils {
  val conf = new SparkConf().setAppName("df").setMaster("local[3]")
  conf.set("spark.yarn.executor.memoryOverhead","4096")
  conf.set("spark.default.parallelism","50")
  val sc = new SparkContext(conf)
  val hiveContext = new HiveContext(sc)

  Class.forName("oracle.jdbc.driver.OracleDriver")
  val url = "jdbc:oracle:thin:@192.168.0.194:1521:smsdb"
  val driver = "oracle.jdbc.driver.OracleDriver"
  val properties = new Properties()
  properties.put("user", "smsdb")
  properties.put("password", "chuanglan789")
  properties.put("driver", driver)


  def getDF(table:String):DataFrame={
    hiveContext.read.jdbc(url,table,properties)
  }

  //分词
  def runAnsjSplit(text:String):Array[String]={
    val parse=ToAnalysis.parse(text)
    val terms=parse.getTerms
    val arr=new ArrayBuffer[String]()
    for(i<-0 to terms.size()-1){
      val word=terms(i).getName
      arr+=word
    }
    arr.toArray
  }

  //计算余弦相似度
/*  def cosineSimilarity(vec1:DenseVector,vec2:DenseVector,f2jBLAS:F2jBLAS): Double = {
    val fenzi=f2jBLAS.ddot(vec1.size, vec1.values, 1, vec2.values, 1)
    val fenmu=Vectors.norm(vec1,2.0)*Vectors.norm(vec2,2.0)
    fenzi/fenmu
  }*/

  def cosineSimilarity(sp1:SparseVector[Double],sp2:SparseVector[Double]):Double={
    sp1.dot(sp2)/(norm(sp1)*norm(sp2))
  }

}
