package clworks

import breeze.linalg.{SparseVector, norm}
import clworks.Utils.hiveContext
import org.apache.log4j.{Level, Logger}
import org.apache.spark.ml.feature.HashingTF
import org.apache.spark.mllib.linalg.{SparseVector => SV}
import org.apache.spark.rdd.{RDD, RDDOperationScope}

//使用HF值计算余弦相似度
object HfSimilarity {
  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR)
    import hiveContext.implicits._

/*    val standID=Utils.getDF("SENSWORD_COUNT_2017").select("CONTENT").rdd.zipWithIndex().map(i=>(i._1.toString(),i._2)).toDF("CONTENT_STD","ID")
    standID.registerTempTable("t1")
    val standArr=Utils.getDF("SENSWORD_COUNT_2017").select("CONTENT")
      .map(i=>(i(0).toString.replaceAll("\\s+","")))
      .map(i=>(i.replaceAll("\\w+","")))
      .map(i=>(i.replaceAll("\\pP","")))
      .filter(i=>i.length>1)  //防止空值
      .map(i=>(new ChineSeg().runAnsjSplitArr(i)))
      .toDS().toDF().toDF("CONTENT")

    val testID=Utils.getDF("SENSWORD_TEST_20171215").select("CONTENT").rdd.zipWithIndex().map(i=>(i._1.toString(),i._2)).toDF("CONTENT_TEST","ID")
    testID.registerTempTable("t2")
    val testArr=Utils.getDF("SENSWORD_TEST_20171215").select("CONTENT")
      .map(i=>(i(0).toString.replaceAll("\\s+","")))
      .map(i=>(i.replaceAll("\\w+","")))
      .map(i=>(i.replaceAll("\\pP","")))
      .filter(i=>i.length>1)
      .map(i=>(new ChineSeg().runAnsjSplitArr(i)))
      .toDS().toDF().toDF("CONTENT")*/

   /* val oriSTD=Utils.getDF("SENSWORD_COUNT_2017").select("CONTENT").limit(10).rdd
      .filter(i=>i(0).toString.replaceAll("[\\s+\\w+\\pP]","").length>1)
      .map(i=>(i(0).toString)).toDF("CONTENT")
    val oriTEST=Utils.getDF("SENSWORD_TEST_20171215").select("CONTENT").limit(10).rdd
      .filter(i=>i(0).toString.replaceAll("[\\s+\\w+\\pP]","").length>1)
      .map(i=>(i(0).toString)).toDF("CONTENT")

    val stand=oriSTD
      .map(i=>(i(0).toString.replaceAll("\\s+","")))
      .map(i=>(i.replaceAll("\\w+","")))
      .map(i=>(i.replaceAll("\\pP","")))
      .filter(i=>i.length>1)
      .toDF("CONTENT")
    val standArr=stand.map(i=>(new ChineSeg().runAnsjSplitArr(i(0).toString))).toDS().toDF().toDF("CONTENT")
    val standID=oriSTD.rdd.zipWithIndex().map(i=>(i._1.toString().substring(i._1.toString().indexOf("[")+1,i._1.toString().indexOf("]")),i._2)).toDF("CONTENT_STD","ID")
    //val standID=oriSTD.rdd.zipWithIndex().map(i=>(i._1.toString(),i._2)).toDF("CONTENT_STD","ID")
    standID.registerTempTable("t1")

    val test=oriTEST
      .map(i=>(i(0).toString.replaceAll("\\s+","")))
      .map(i=>(i.replaceAll("\\w+","")))
      .map(i=>(i.replaceAll("\\pP","")))
      .filter(i=>i.length>1)
      .toDF("CONTENT")
    val testArr=test.map(i=>(new ChineSeg().runAnsjSplitArr(i(0).toString))).toDS().toDF().toDF("CONTENT")
    val testID=oriTEST.rdd.zipWithIndex().map(i=>(i._1.toString().substring(i._1.toString().indexOf("[")+1,i._1.toString().indexOf("]")),i._2)).toDF("CONTENT_TEST","ID")
    //val testID=Utils.getDF("SENSWORD_TEST_20171215").select("CONTENT").limit(5).rdd.zipWithIndex().map(i=>(i._1.toString(),i._2)).toDF("CONTENT_TEST","ID")
    testID.registerTempTable("t2")

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
   // val standBroad=Utils.sc.broadcast(standIndex.take(standIndex.count().toInt))

    /**
      * collect作用：从远程集群是拉取数据到本地
      * broadcast：将数据从一个节点发送到其他各个节点上去，这里如果不使用broadcast将导致错误
      */
     //val standBroad=Utils.sc.broadcast(standIndex.collect())

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

/*    val cosDF=testIndex.flatMap{
      case(id1,test)=>
        standBroad.map{
          case(id2,stand)=>
            val cosineSimilarity=Utils.cosineSimilarity(test,stand)
            (id1,id2,cosineSimilarity)
        }
    }
      .toDF("TESTID","STANDARDID","SIMILARITY")*/

/*rdd不能嵌套
    testIndex.foreach{
      case(id1,test)=>
        standIndex.map{
          case(id2,stand)=>
            val cosineSimilarity=Utils.cosineSimilarity(test,stand)
            println(id1,id2,cosineSimilarity)
        }
    }
*/


    //广播小表降低网络开销,在2.1版本不能broadcast
    val testBroad=Utils.sc.broadcast(testIndex)
    //重新分区
    val rdd1=testBroad.value.cartesian(standIndex)
      //.coalesce(args(2).toInt,shuffle = true)

    //println(rdd1.partitions.length)

    val cosDF=rdd1.map{
      case((id1,sp1),(id2,sp2))=>
        val cosineSimilarity=sp1.dot(sp2)/ (norm(sp1) * norm(sp2))
        (id1,id2,cosineSimilarity)
    }
      .toDF("TESTID","STANDARDID","SIMILARITY")





    cosDF.show(false)

    cosDF.registerTempTable("t3")

    /**
      * 提高shuffle操作的reduce并行度,可以让每个reduce task分配到更少的数据量
      * shuffle算子，比如groupByKey、countByKey、reduceByKey。在调用的时候，可传入一个参数。代表shuffle操作的reduce端的并行度
      */
    val df1=cosDF.select("TESTID","SIMILARITY").map(i=>(i(0).toString.toDouble,i(1).toString.toDouble)).reduceByKey((i,j)=>Math.max(i,j)).toDF("ID","SIMILARITY")
    df1.registerTempTable("t4")


    val unionDF=hiveContext.sql("select t3.TESTID,t3.STANDARDID,t4.SIMILARITY from t3,t4 where t3.SIMILARITY=t4.SIMILARITY").distinct()
    unionDF.registerTempTable("t5")

    //unionDF.show(false)

    val contentSim=hiveContext.sql("select t1.CONTENT_STD,t2.CONTENT_TEST,t5.SIMILARITY from t1,t2,t5 where t1.ID=t5.STANDARDID and t2.ID=t5.TESTID")
*/
    //contentSim.show(100,false)

   // contentSim.write.mode("append").jdbc(Utils.url,"SENSWORD_T",Utils.properties)


    //hiveContext.sql("select t2.CONTENT_TEST,t4.SIMILARITY from t2,t4 where t2.ID=t4.ID")
     // .write.mode("append").jdbc(Utils.url,"SENSWORD_COUNT_SIM",Utils.properties)

    //spark-submit --master yarn --deploy-mode cluster --driver-memory 2g  --num-executors 10  --executor-memory 6g  --executor-cores 3 --class chuanglanML.HfSimilarity  --jars ojdbc6.jar,ansj_seg-5.1.3.jar,breeze_2.10-0.11.2.jar,nlp-lang-1.7.2.jar    chuanglan.jar

  }
}
