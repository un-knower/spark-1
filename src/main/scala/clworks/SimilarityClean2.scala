package clworks

import breeze.linalg.{SparseVector, norm}
import clworks.Utils.hiveContext
import org.apache.log4j.{Level, Logger}
import org.apache.spark.mllib.feature.{HashingTF, IDF}
import org.apache.spark.mllib.linalg.{SparseVector => SV}

import scala.collection.mutable.ArrayBuffer

object SimilarityClean2 {
  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR)
    import hiveContext.implicits._
    val standard=Utils.getDF("SENSWORD_COUNT_2017_1").na.fill("0")
      .map(i=>(i(0).toString,i(1).toString,i(2).toString,i(3).toString,i(4).toString.replaceAll("\\s+","")))
      .map(i=>(i._1,i._2,i._3,i._4,i._5.replaceAll("\\w+","")))
      .map(i=>(i._1,i._2,i._3,i._4,i._5.replaceAll("\\pP","")))
      .toDF("ACCOUNT","SP","REPORT","NUM","CONTENT")
    val compare=Utils.getDF("SENSWORD_TEST_20171215_1").na.fill("0")
      .map(i=>(i(0).toString.replaceAll("\\s+",""),i(1).toString,i(2).toString))
      .map(i=>(i._1.replaceAll("\\w+",""),i._2,i._3))
      .map(i=>(i._1.replaceAll("\\pP",""),i._2,i._3))
      .toDF("CONTENT","SP","ACCOUNT")
    val standardArr=standard.rdd.filter(i=>i(4).toString.length>1)
      .map(i=>(i(0).toString,i(1).toString,i(2).toString,i(3).toString,new ChineSeg().runAnsjSplitArr(i(4).toString)))
      .toDF("ACCOUNT","SP","REPORT","NUM","CONTENT").select("CONTENT")
    val compareArr=compare.rdd.filter(i=>i(0).toString.length>1)
      .map(i=>(new ChineSeg().runAnsjSplitArr(i(0).toString),i(1).toString,i(2).toString))
      .toDF("CONTENT","SP","ACCOUNT").select("CONTENT")

    val hashingTF=new HashingTF()
/*
    val tf_s=standardArr.map{
      case(content,id)=>
        val tf=hashingTF.transform(content)
        (id,tf)
    }
    val tf_t=compareArr.map{
      case(content,id)=>
        val tf=hashingTF.transform(content)
        (id,tf)
    }

    val idf_sModel=new IDF().fit(tf_s.values)
    val idf_tModel=new IDF().fit(tf_t.values)
    val idf_s=tf_s.mapValues(idf_sModel.transform(_))
    val idf_t=tf_t.mapValues(idf_tModel.transform(_))

    val idf_sA=idf_s.collect()

    idf_sA.foreach(println(_))

    idf_t.flatMap{
      case (id1, idf1) =>
        val sv1 = idf1.asInstanceOf[SV]
        val bsv1 = new SparseVector[Double](sv1.indices, sv1.values, sv1.size)
        idf_sA map{
          case (id2, idf2) =>
            val sv2 = idf2.asInstanceOf[SV]
            val bsv2 = new SparseVector[Double](sv2.indices, sv2.values, sv2.size)
            val cosSim = bsv1.dot(bsv2)/ (norm(bsv1) * norm(bsv2))
            (id1, id2, cosSim)
        }
    }
     // .foreach(println(_))*/


    standardArr




  }
}
