package clworks

import clworks.Utils.hiveContext
import org.apache.log4j.{Level, Logger}
import org.apache.spark.ml.feature.{HashingTF, IDF}
import org.apache.spark.mllib.linalg.{SparseVector => SV}

object DocAppend {
  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR)
    import hiveContext.implicits._
    val standardID=Utils.getDF("SENSWORD_COUNT_2017_1").select("CONTENT")
    val standardCount=standardID.count()
    val compareID=Utils.getDF("SENSWORD_TEST_20171215_1").select("CONTENT")
    //unionAll没有去重效果，先把不同的短信内容组成一个文档
    val unionID=standardID.unionAll(compareID).rdd.zipWithIndex().map(i=>(i._1.toString(),i._2)).toDF("CONTENT","ID")
/*    union.registerTempTable("t")
    hiveContext.sql(s"select * from t where id=${standardCount-1}").show()*/

    val unionSep=unionID.rdd.filter(i=>i(0).toString.length>1)
      .map(i=>(i(0).toString.replaceAll("\\s+",""),i(1).toString))
      .map(i=>(i._1.replaceAll("\\w+",""),i._2))
      .map(i=>(i._1.replaceAll("\\pP",""),i._2))
      .map(i=>(new ChineSeg().runAnsjSplitArr(i._1),i._2))
      .toDF("CONTENT","ID")

    val hashingTF=new HashingTF()
      .setInputCol("CONTENT")
      .setOutputCol("rawFeatures")
    val featurizedData = hashingTF.transform(unionSep).select("ID","rawFeatures")



  }
}
