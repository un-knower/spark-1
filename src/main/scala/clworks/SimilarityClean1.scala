package clworks

import breeze.linalg.{SparseVector, norm}
import clworks.Utils.hiveContext
import org.apache.log4j.{Level, Logger}
import org.apache.spark.ml.feature.{HashingTF, IDF}
import org.apache.spark.mllib.linalg.{SparseVector => SV}

import scala.collection.mutable.ArrayBuffer

object SimilarityClean1 {
  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR)
    import hiveContext.implicits._
    //ID时long类型
    val standardID=Utils.getDF("SENSWORD_COUNT_2017_1").rdd.zipWithIndex().map(i=>(i._1.toString(),i._2)).toDF("CONTENT_STD","ID")
    //standardID.registerTempTable("t1")
    val compareID=Utils.getDF("SENSWORD_TEST_20171215_1").rdd.zipWithIndex().map(i=>(i._1.toString(),i._2)).toDF("CONTENT_TEST","ID")
    compareID.registerTempTable("t2")
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
      .toDF("ACCOUNT","SP","REPORT","NUM","CONTENT_S").select("CONTENT_S")
    val compareArr=compare.rdd.filter(i=>i(0).toString.length>1)
      .map(i=>(new ChineSeg().runAnsjSplitArr(i(0).toString),i(1).toString,i(2).toString))
      .toDF("CONTENT_T","SP","ACCOUNT").select("CONTENT_T")
    val hashingTF_S=new HashingTF()
      .setInputCol("CONTENT_S")
      .setOutputCol("rawFeatures_s")
    val featurizedData_S = hashingTF_S.transform(standardArr)
   /* val idf_s=new IDF().setInputCol("rawFeatures_s").setOutputCol("features_s")
    val idfModel_s = idf_s.fit(featurizedData_S)
    val rescaledData_S = idfModel_s.transform(featurizedData_S)*/
    val hashingTF_T=new HashingTF()
      .setInputCol("CONTENT_T")
      .setOutputCol("rawFeatures_t")
    val featurizedData_T = hashingTF_T.transform(compareArr)
    /*val idf_t=new IDF().setInputCol("rawFeatures_t").setOutputCol("features_t")
    val idfModel_t = idf_t.fit(featurizedData_T)
    val rescaledData_T = idfModel_t.transform(featurizedData_T)*/
    val standIndex=featurizedData_S.select("rawFeatures_s").rdd.zipWithIndex()
      .map{
        case(row,id)=>
          val sparse=row.getAs(0).asInstanceOf[SV]
          val standBreeze=new SparseVector[Double](sparse.indices, sparse.values, sparse.size)
          (id,standBreeze)
      }
    val standBroad=Utils.sc.broadcast(standIndex.take(standIndex.count().toInt))
    val standardToArr=standIndex.collect()
    val testIndex=featurizedData_T.select("rawFeatures_t").rdd.zipWithIndex()
      .map{
        case(row,id)=>
          val sparse=row.getAs(0).asInstanceOf[SV]
          val testBreeze=new SparseVector[Double](sparse.indices, sparse.values, sparse.size)
          (id,testBreeze)
      }

    val testBroad=Utils.sc.broadcast(testIndex.collect())

    val cosDF=testIndex.flatMap{
      case(id1,test)=>
        val standVal=standBroad.value
        standVal.map{
          case(id2,stand)=>
            val cosineSimilarity=test.dot(stand)/ (norm(test) * norm(stand))
            (id1,id2,cosineSimilarity)
        }
    }
      .toDF("TESTID","STANDARDID","SIMILARITY")

/*    val cosDF1=testBroad.value.map(_._2)
    val cosDF2=standardToArr.map(_._2)
    var results=new ArrayBuffer[(Int,Int,Double)]()
    for(i<- 0 to cosDF1.length-1){
      for(j<- 0 to cosDF2.length-1){
        val cosineSimilarity=cosDF1(i).dot(cosDF2(j))/(norm(cosDF1(i)) * norm(cosDF2(j)))
        results+=((i,j,cosineSimilarity))
      }
    }*/

    //当为单条记录时idf值都为0
    cosDF.show(false)
   // testIndex.foreach(println(_))
    //cosDF.show(1000,false)

/*    val df1=cosDF.select("TESTID","SIMILARITY").map(i=>(i(0).toString.toDouble,i(1).toString.toDouble)).reduceByKey((i,j)=>Math.max(i,j)).toDF("ID","SIMILARITY")
    df1.registerTempTable("t3")
    cosDF.registerTempTable("t4")
    val df2=Utils.hiveContext.sql("select t4.TESTID ,t4.STANDARDID,t4.SIMILARITY from t3,t4 where t3.SIMILARITY=t4.SIMILARITY")*/

    //df2.show(1000,false)



/*    cosDF.select("TESTID","SIMILARITY").map(i=>(i(0).toString.toDouble,i(1).toString.toDouble)).reduceByKey((i,j)=>Math.max(i,j)).toDF("ID","SIMILARITY").orderBy("SIMILARITY")
      .registerTempTable("t2")*/
   // Utils.hiveContext.sql("select t1.CONTENT_TEST ,t2.SIMILARITY from t1,t2 where t1.ID=t2.ID").orderBy("SIMILARITY").show(1000,false)

   // Utils.sc.parallelize(results).toDF().show()
    //cosDF.select("TESTID","SIMILARITY").map(i=>(i(0).toString.toDouble,i(1).toString.toDouble)).reduceByKey((i,j)=>Math.max(i,j)).toDF("ID","SIMILARITY")
  }
}
