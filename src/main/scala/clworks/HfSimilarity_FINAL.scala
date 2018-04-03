package clworks

import breeze.linalg.{SparseVector, norm}
import clworks.Utils.hiveContext
import org.apache.log4j.{Level, Logger}
import org.apache.spark.ml.feature.HashingTF
import org.apache.spark.mllib.linalg.{SparseVector => SV}
import org.apache.spark.sql.types._

import scala.collection.mutable

object HfSimilarity_FINAL {
  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR)
    import hiveContext.implicits._

    val standardTable=Utils.getDF("SENSWORD_COUNT_2017").select("CONTENT").rdd
      .filter(i=>i(0).toString.replaceAll("[^\\u4e00-\\u9fa5]","").length>1)
      .map(i=>(i(0).toString)).toDF("CONTENT").limit(5).cache()
    val testTable=Utils.getDF("SENSWORD_TEST_20171215").select("CONTENT").rdd
      .filter(i=>i(0).toString.replaceAll("[^\\u4e00-\\u9fa5]","").length>1)
      .map(i=>(i(0).toString)).toDF("CONTENT").limit(5).cache()

    //标准表：分词后的数组形式
   /* val oriSTD=standardTable
      .map(i=>(i(0).toString.replaceAll("[^\\u4e00-\\u9fa5]","")))
      .filter(_.length>1)
      .map(i=>(new ChineSeg().runAnsjSplitNSArr(i)))
      .toDS().toDF().toDF("CONTENT")
      .distinct()

    standardTable
      .map(i=>(i(0).toString.replaceAll("[^\\u4e00-\\u9fa5]","")))
      .filter(_.length>1)
      .map(i=>(new ChineSeg().runAnsjSplit(i)))
      .toDF("CONTENT")
      .rdd
      .zipWithIndex()
      .map(i=>(i._1.toString().substring(i._1.toString().indexOf("[")+1,i._1.toString().indexOf("]")),i._2))
      .toDF("CONTENT_STD","IDS")
      .registerTempTable("ss")


    //标准表：分词后的字符串形式，并生成唯一id
    val standardID=oriSTD.rdd
      .zipWithIndex()
      .map(i=>(i._1.toString().substring(i._1.toString().indexOf("(")+1,i._1.toString().indexOf(")")),i._2))
      .toDF("CONTENT_STD","IDS")
    standardID.registerTempTable("t1")

    //测试表：分词后的数组形式
    val oriTEST=testTable
      .map(i=>(i(0).toString.replaceAll("[^\\u4e00-\\u9fa5]","")))
      .filter(_.length>1)
      .map(i=>(new ChineSeg().runAnsjSplitNSArr(i)))
      .toDS().toDF().toDF("CONTENT")
      .distinct()
    //测试表：分词后的字符串形式，并生成唯一id
    val testID=oriTEST.rdd
      .zipWithIndex()
      .map(i=>(i._1.toString().substring(i._1.toString().indexOf("(")+1,i._1.toString().indexOf(")")),i._2))
      .toDF("CONTENT_TEST","IDT")
    testID.registerTempTable("t2")

    //生成标准和测试的hf值
    val hashingTF_S=new HashingTF()
      .setInputCol("CONTENT")
      .setOutputCol("rawFeatures_s")
    val featurizedData_S = hashingTF_S.transform(oriSTD)
    val hashingTF_T=new HashingTF()
      .setInputCol("CONTENT")
      .setOutputCol("rawFeatures_t")
    val featurizedData_T = hashingTF_T.transform(oriTEST)

    //标准表的hf值转换为 breeze.linalg.SparseVector
    val standardSparse=featurizedData_S.select("rawFeatures_s").rdd.zipWithIndex()
      .map{
        case(row,id)=>
          val sparse=row.getAs(0).asInstanceOf[SV]
          val standBreeze=new SparseVector[Double](sparse.indices, sparse.values, sparse.size)
          (id,standBreeze)
      }

    //测试表的hf值转换为 breeze.linalg.SparseVector
    val testSparse=featurizedData_T.select("rawFeatures_t").rdd.zipWithIndex()
      .map{
        case(row,id)=>
          val sparse=row.getAs(0).asInstanceOf[SV]
          val testBreeze=new SparseVector[Double](sparse.indices, sparse.values, sparse.size)
          (id,testBreeze)
      }

    //先进行笛卡尔积，再分别计算余弦相似度
    val testBroad=Utils.sc.broadcast(testSparse)
    val cartesianRdd=testBroad.value.cartesian(standardSparse)
    val cosineSimilarity=cartesianRdd.map{
      case((id1,sp1),(id2,sp2))=>
        val cosineSimilarity=sp1.dot(sp2)/ (norm(sp1) * norm(sp2))
        (id1,id2,cosineSimilarity)
    }
      .toDF("TESTID","STANDARDID","SIMILARITY")
    cosineSimilarity.registerTempTable("t3")

    //取出标准表和测试表的id，和对应的最大相似度
    val maxSimilarity=cosineSimilarity.select("TESTID","SIMILARITY").map(i=>(i(0).toString.toDouble,i(1).toString.toDouble)).reduceByKey((i,j)=>Math.max(i,j)).toDF("ID","SIMILARITY")
    maxSimilarity.registerTempTable("t4")
    val unionDF=hiveContext.sql("select t3.TESTID,t3.STANDARDID,t4.SIMILARITY from t3,t4 where t3.SIMILARITY=t4.SIMILARITY").distinct()
    unionDF.registerTempTable("t5")

    //还原原始内容
    //val contentCompare=hiveContext.sql("select t1.CONTENT_STD,t2.CONTENT_TEST,t5.SIMILARITY from t1,t2,t5 where t1.IDS=t5.STANDARDID and t2.IDT=t5.TESTID")
    val contentCompare=hiveContext.sql("select ss.CONTENT_STD,t5.SIMILARITY from ss,t5 where ss.IDS=t5.STANDARDID")
    contentCompare.registerTempTable("t6")

    hiveContext.sql("select * from ss").show(false)
    hiveContext.sql("select * from t6").show(false)*/
   // hiveContext.sql("select ss.CONTENT,t6.SIMILARITY from ss,t6 where ss.CONTENT=t6.CONTENT_STD").show(false)


/*    cosineSimilarity.show(100,false)
    unionDF.show(false)
    standardID.show(false)
    testID.show(false)
    contentCompare.show(false)*/


  }
}
