package mathCompute

import java.util.Properties

import breeze.linalg.{SparseVector, norm}
import org.ansj.splitWord.analysis.ToAnalysis
import org.apache.spark.ml.feature.HashingTF
import org.apache.spark.sql.SparkSession
import org.apache.spark.ml.linalg.{SparseVector => SV}

import scala.collection.mutable.ArrayBuffer
import scala.collection.JavaConversions._


object HfCosnSimilarity {
  def main(args: Array[String]): Unit = {
    val ss=SparkSession.builder().appName("test").master("local[3]")
      .getOrCreate()
    import ss.implicits._
    Class.forName("oracle.jdbc.driver.OracleDriver")
    val url = "jdbc:oracle:thin:@192.168.0.194:1521:smsdb"
    val driver = "oracle.jdbc.driver.OracleDriver"
    val properties = new Properties()
    properties.put("user", "smsdb")
    properties.put("password", "chuanglan789")
    properties.put("driver", driver)
    val oriSTD=ss.read.jdbc(url,"SENSWORD_COUNT_2017",properties).select("CONTENT").limit(10).rdd
      .filter(i=>i(0).toString.replaceAll("[\\s+\\w+\\pP]","").length>1)
      .map(i=>(i(0).toString)).toDF("CONTENT")
    val oriTEST= ss.read.jdbc(url,"SENSWORD_TEST_20171215",properties).select("CONTENT").limit(10).rdd
      .filter(i=>i(0).toString.replaceAll("[\\s+\\w+\\pP]","").length>1)
      .map(i=>(i(0).toString)).toDF("CONTENT")
    val stand=oriSTD
      .map(i=>(i(0).toString.replaceAll("\\s+","")))
      .map(i=>(i.replaceAll("\\w+","")))
      .map(i=>(i.replaceAll("\\pP","")))
      .filter(i=>i.length>1)
      .toDF("CONTENT")

    def runAnsjSplitArr(text:String):Array[String]={
      val parse=ToAnalysis.parse(text)
      val terms=parse.getTerms
      val arr=new ArrayBuffer[String]()
      for(i<-0 to terms.size()-1){
        val word=terms(i).getName
        arr+=word
      }
      arr.toArray
    }

    val standArr=stand.map(i=>(runAnsjSplitArr(i(0).toString))).toDF("CONTENT")
    val standID=oriSTD.rdd.zipWithIndex().map(i=>(i._1.toString().substring(i._1.toString().indexOf("[")+1,i._1.toString().indexOf("]")),i._2)).toDF("CONTENT_STD","ID")
    val test=oriTEST
      .map(i=>(i(0).toString.replaceAll("\\s+","")))
      .map(i=>(i.replaceAll("\\w+","")))
      .map(i=>(i.replaceAll("\\pP","")))
      .filter(i=>i.length>1)
      .toDF("CONTENT")
    val testArr=test.map(i=>(runAnsjSplitArr(i(0).toString))).toDF("CONTENT")
    val testID=oriTEST.rdd.zipWithIndex().map(i=>(i._1.toString().substring(i._1.toString().indexOf("[")+1,i._1.toString().indexOf("]")),i._2)).toDF("CONTENT_TEST","ID")


    val hashingTF_S=new HashingTF()
      .setInputCol("CONTENT")
      .setOutputCol("rawFeatures_s")
    val featurizedData_S = hashingTF_S.transform(standArr)
    val hashingTF_T=new HashingTF()
      .setInputCol("CONTENT")
      .setOutputCol("rawFeatures_t")
    val featurizedData_T = hashingTF_T.transform(testArr)

    val standIndex=featurizedData_S.select("rawFeatures_s").rdd.zipWithIndex()
      .map{
        case(row,id)=>
          val sparse=row.getAs(0).asInstanceOf[SV]
          val standBreeze=new SparseVector[Double](sparse.indices, sparse.values, sparse.size)
          (id,standBreeze)
      }

    /**
      * collect作用：从远程集群是拉取数据到本地
      * broadcast：将数据从一个节点发送到其他各个节点上去，这里如果不使用broadcast将导致错误
      */
    val standBroad=ss.sparkContext.broadcast(standIndex.collect())
    val testIndex=featurizedData_T.select("rawFeatures_t").rdd.zipWithIndex()
      .map{
        case(row,id)=>
          val sparse=row.getAs(0).asInstanceOf[SV]
          val testBreeze=new SparseVector[Double](sparse.indices, sparse.values, sparse.size)
          (id,testBreeze)
      }

/*    val cosDF=testIndex.flatMap{
      case(id1,test)=>
        val standVal=standBroad.value
        standVal.map{
          case(id2,stand)=>
            val cosineSimilarity=test.dot(stand)/ (norm(test) * norm(stand))
            (id1,id2,cosineSimilarity)
        }
    }
      .toDF("TESTID","STANDARDID","SIMILARITY")*/


    //因为需要比较两两的相似度，所以可以使用笛卡尔积，但此法很消耗资源，需要重新分区，降低每个分区的数据量
    val rdd=testIndex.cartesian(standIndex)
      //.coalesce(10,shuffle = true)

    val cosDF=rdd.map{
      case((id1,sp1),(id2,sp2))=>
        val cosineSimilarity=sp1.dot(sp2)/ (norm(sp1) * norm(sp2))
        (id1,id2,cosineSimilarity)
    }
      .toDF("TESTID","STANDARDID","SIMILARITY")

    cosDF.show(false)

  }
}
