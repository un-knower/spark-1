package clworks

import breeze.linalg._
import clworks.Utils.hiveContext
import com.github.fommil.netlib.F2jBLAS
import org.apache.log4j.{Level, Logger}
import org.apache.spark.ml.feature.{HashingTF, IDF}
import org.apache.spark.mllib.linalg.{SparseVector=>SV}

import scala.collection.JavaConversions._
import scala.collection.mutable.ArrayBuffer

object Similarity {
  def main(args: Array[String]): Unit = {

    //Logger.getLogger("org").setLevel(Level.ERROR)

    import hiveContext.implicits._

    /**
      * 1.去除空格、数字、标点
      */

    val standard=Utils.getDF("SENSWORD_COUNT_2017").na.fill("0")
      .map(i=>(i(0).toString,i(1).toString,i(2).toString,i(3).toString,i(4).toString.replaceAll("\\s+","")))
      .map(i=>(i._1,i._2,i._3,i._4,i._5.replaceAll("\\w+","")))
      .map(i=>(i._1,i._2,i._3,i._4,i._5.replaceAll("\\pP","")))
      .toDF("ACCOUNT","SP","REPORT","NUM","CONTENT")

    val standardID=Utils.getDF("SENSWORD_COUNT_2017").rdd.zipWithIndex().map(i=>(i._1.toString(),i._2)).toDF("CONTENT_STD","ID")

    val compare=Utils.getDF("SENSWORD_TEST_20171215").na.fill("0")
      .map(i=>(i(0).toString.replaceAll("\\s+",""),i(1).toString,i(2).toString))
      .map(i=>(i._1.replaceAll("\\w+",""),i._2,i._3))
      .map(i=>(i._1.replaceAll("\\pP",""),i._2,i._3))
      .toDF("CONTENT","SP","ACCOUNT")

    val compareID=Utils.getDF("SENSWORD_TEST_20171215").rdd.zipWithIndex().map(i=>(i._1.toString(),i._2)).toDF("CONTENT_TEST","ID")


    /**
      * 2.进行分词，转换为数组，进入下一步hf-idf操作
      */
    val standardArr=standard.rdd.filter(i=>i(4).toString.length>1)
      .map(i=>(i(0).toString,i(1).toString,i(2).toString,i(3).toString,new ChineSeg().runAnsjSplitArr(i(4).toString)))
      .toDF("ACCOUNT","SP","REPORT","NUM","CONTENT_S").select("CONTENT_S").limit(10).cache()


    val compareArr=compare.rdd.filter(i=>i(0).toString.length>1)
      .map(i=>(new ChineSeg().runAnsjSplitArr(i(0).toString),i(1).toString,i(2).toString))
      .toDF("CONTENT_T","SP","ACCOUNT").select("CONTENT_T").limit(5).cache()



    /**
      * 3.计算余弦相似度。注意：每行作为一个document
      */

      //标准tf_idf
    val hashingTF_S=new HashingTF()
      .setInputCol("CONTENT_S")
      .setOutputCol("rawFeatures_s")
    val featurizedData_S = hashingTF_S.transform(standardArr)
    val idf_s=new IDF().setInputCol("rawFeatures_s").setOutputCol("features_s")
    val idfModel_s = idf_s.fit(featurizedData_S)
    val rescaledData_S = idfModel_s.transform(featurizedData_S)

    //对比tf-idf
    val hashingTF_T=new HashingTF()
      .setInputCol("CONTENT_T")
      .setOutputCol("rawFeatures_t")
    val featurizedData_T = hashingTF_T.transform(compareArr)
    val idf_t=new IDF().setInputCol("rawFeatures_t").setOutputCol("features_t")
    val idfModel_t = idf_t.fit(featurizedData_T)
    val rescaledData_T = idfModel_t.transform(featurizedData_T)

    /**
      * 为每行标记唯一id，从0开始
      * 将row转换为稀疏向量
      * 1.row类型：([(262144,[3541,20013,20803,23478,24744,25237,26377,26410,27142,30340,34701,35831,37329,47188,96721,113898,118840,168463,170460,170864,178234,209463,232098,232270],[1.7047480922384253,1.2992829841302609,1.0116009116784799,1.7047480922384253,1.0116009116784799,1.7047480922384253,0.7884573603642703,1.2992829841302609,3.4094961844768505,1.212271607140631,1.7047480922384253,1.7047480922384253,3.4094961844768505,1.7047480922384253,1.2992829841302609,1.7047480922384253,1.2992829841302609,1.2992829841302609,3.4094961844768505,1.7047480922384253,1.7047480922384253,1.7047480922384253,1.7047480922384253,1.7047480922384253])],0)
      * 2.SparseVector类型：(0,(262144,[3541,20013,20803,23478,24744,25237,26377,26410,27142,30340,34701,35831,37329,47188,96721,113898,118840,168463,170460,170864,178234,209463,232098,232270],[1.7047480922384253,1.2992829841302609,1.0116009116784799,1.7047480922384253,1.0116009116784799,1.7047480922384253,0.7884573603642703,1.2992829841302609,3.4094961844768505,1.212271607140631,1.7047480922384253,1.7047480922384253,3.4094961844768505,1.7047480922384253,1.2992829841302609,1.7047480922384253,1.2992829841302609,1.2992829841302609,3.4094961844768505,1.7047480922384253,1.7047480922384253,1.7047480922384253,1.7047480922384253,1.7047480922384253]))
      * 3.转换为breeze.linalg包中的SparseVector，以便使用余弦公式,注意类重命名:(0,SparseVector((3541,1.7047480922384253), (20013,1.2992829841302609), (20803,1.0116009116784799), (23478,1.7047480922384253), (24744,1.0116009116784799), (25237,1.7047480922384253), (26377,0.7884573603642703), (26410,1.2992829841302609), (27142,3.4094961844768505), (30340,1.212271607140631), (34701,1.7047480922384253), (35831,1.7047480922384253), (37329,3.4094961844768505), (47188,1.7047480922384253), (96721,1.2992829841302609), (113898,1.7047480922384253), (118840,1.2992829841302609), (168463,1.2992829841302609), (170460,3.4094961844768505), (170864,1.7047480922384253), (178234,1.7047480922384253), (209463,1.7047480922384253), (232098,1.7047480922384253), (232270,1.7047480922384253)))
      *
      *
      */
    val standIndex=rescaledData_S.select("features_s").rdd.zipWithIndex()
      .map{
        case(row,id)=>
          val sparse=row.getAs(0).asInstanceOf[SV]
          val standBreeze=new SparseVector[Double](sparse.indices, sparse.values, sparse.size)
          (id,standBreeze)
      }

    val standIndexDF=rescaledData_S.select("features_s").rdd.zipWithIndex().map(i=>(i._1.toString(),i._2)).toDF("STANDARD_VECTOR","ID")

    //使用take或collect转换为Array

    //val standBroad=Utils.sc.broadcast(standIndex.take(standIndex.count().toInt))
    val standardToArr=standIndex.collect()

    /**
      *  Schema for type breeze.linalg.SparseVector[Double] is not supported，需要使用toString()
      *  |0  |SparseVector((3541,1.7047480922384253), (20013,1.2992829841302609), (20803,1.0116009116784799), (23478,1.7047480922384253), (24744,1.0116009116784799), (25237,1.7047480922384253), (26377,0.7884573603642703), (26410,1.2992829841302609), (27142,3.4094961844768505), (30340,1.212271607140631), (34701,1.7047480922384253), (35831,1.7047480922384253), (37329,3.4094961844768505), (47188,1.7047480922384253), (96721,1.2992829841302609), (113898,1.7047480922384253), (118840,1.2992829841302609), (168463,1.2992829841302609), (170460,3.4094961844768505), (170864,1.7047480922384253), (178234,1.7047480922384253), (209463,1.7047480922384253), (232098,1.7047480922384253), (232270,1.7047480922384253))                                                                                                                                                                                                     |
      *
      */
     // .toDF("ID","STANDARD_VECTOR")


    val testIndex=rescaledData_T.select("features_t").rdd.zipWithIndex()
      .map{
        case(row,id)=>
          val sparse=row.getAs(0).asInstanceOf[SV]
          val testBreeze=new SparseVector[Double](sparse.indices, sparse.values, sparse.size)
          (id,testBreeze)
      }

    val testBroad=Utils.sc.broadcast(testIndex.collect())


    val testIndexDF=rescaledData_T.select("features_t").rdd.zipWithIndex().map(i=>(i._1.toString(),i._2)).toDF("TEST_VECTOR","ID")

    /**
      *
      */

    //rdd的map不能嵌套，但可以嵌套其他集合的map
/*   testIndex.map{
     case(id1,test)=>
       standIndex.map{
         case(id2,standard)=>
           val cosSim = test.dot(standard)/ (norm(test) * norm(standard))
           (id1,id2,cosSim)
       }
   }
      .foreach(println(_))*/


/*    val cosDF=testIndex.flatMap{
      case(id1,test)=>
        val standVal=standBroad.value
        standVal.map{
          case(id2,stand)=>
            val cosineSimilarity=test .dot(stand)/ (norm(test) * norm(stand))
            (id1,id2,cosineSimilarity)
        }
    }
      .toDF("TESTID","STANDARDID","SIMILARITY")*/


    //将SparseVector[Double]存储在数组
    val cosDF1=testBroad.value.map(_._2)
    val cosDF2=standardToArr.map(_._2)
    for(i<- 0 to cosDF1.length-1){
      for(j<- 0 to cosDF2.length-1){
        println((i,j,cosDF1(i).dot(cosDF2(j))/(norm(cosDF1(i)) * norm(cosDF2(j)))))
      }
    }




/*    cosDF.select("TESTID","SIMILARITY").map(i=>(i(0).toString.toDouble,i(1).toString.toDouble)).reduceByKey((i,j)=>Math.max(i,j)).toDF("ID","SIMILARITY")
      .registerTempTable("t2")

    compareID.registerTempTable("t1")


    Utils.hiveContext.sql("select t1.CONTENT_TEST ,t2.SIMILARITY from t1,t2 where t1.ID=t2.ID")
      .write.mode("append").jdbc(Utils.url,"SENSWORD_COUNT_SIMIa",Utils.properties)*/



  }
}
