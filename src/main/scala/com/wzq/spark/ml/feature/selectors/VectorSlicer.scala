package com.wzq.spark.ml.feature.selectors

import java.util.Arrays

import org.apache.spark.ml.attribute.{Attribute, AttributeGroup, NumericAttribute}
import org.apache.spark.ml.feature.VectorSlicer
import org.apache.spark.ml.linalg.Vectors
import org.apache.spark.sql.types.StructType
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.{Row, SparkSession}

/**
  * 切割原向量输出原特征向量的子集
  */
object VectorSlicer {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("mlib").setMaster("local[2]")
    val sc = new SparkContext(conf)
    val ss = SparkSession.builder().getOrCreate()
    val data = Arrays.asList(
      Row(Vectors.sparse(3, Seq((0, -2.0), (1, 2.3)))),
      Row(Vectors.dense(-2.0, 2.3, 0.0))
    )
    val defaultAttr = NumericAttribute.defaultAttr
    val attrs = Array("f1", "f2", "f3").map(defaultAttr.withName)
    val attrGroup = new AttributeGroup("userFeatures", attrs.asInstanceOf[Array[Attribute]])
    val dataset = ss.createDataFrame(data, StructType(Array(attrGroup.toStructField())))

    val slicer = new VectorSlicer().setInputCol("userFeatures").setOutputCol("features")

    slicer.setIndices(Array(1)).setNames(Array("f3"))
    slicer.transform(dataset).show(false)
    /**
    +--------------------+-------------+
|userFeatures        |features     |
+--------------------+-------------+
|(3,[0,1],[-2.0,2.3])|(2,[0],[2.3])|
|[-2.0,2.3,0.0]      |[2.3,0.0]    |
+--------------------+-------------+
      */
  }
}
