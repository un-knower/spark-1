package clworks.optimize

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SQLContext

import scala.collection.mutable.{ArrayBuffer, ListBuffer}

case class People(id:Int,name:String,age:Int)

object RddMappartition {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("partition").setMaster("local[2]")
    val sc = new SparkContext(conf)
    val sQLContext=new SQLContext(sc)
    import sQLContext.implicits._

    /**
      * mapPartitions对比
      * 1.应用于每个分区，而map应用于rdd每个元素
      * 2.map的输入函数中元素为rdd元素值
      * 3.mapPartitions的输入函数中为迭代子Iterator ,返回类型也是Iterator
      * 4.arrayBuffer添加的是元组
      */

    val peopleDF=sc.textFile("data/people.txt",3).map(_.split(","))
      .mapPartitions(i=>{
        val arrayBuffer=new ListBuffer[(Int,String,Int)]()
        while (i.hasNext){
          val j=i.next()
          arrayBuffer+=((j(0).toInt,j(1),j(2).toInt))
        }
        arrayBuffer.iterator
      })
      .map(p=>People(p._1,p._2,p._3))
      .toDF("id","name","age")

    //按照分区索引统计每个分区内的总和
    val peopleDFwithIndex=sc.parallelize(1 to 10,2)
      .mapPartitionsWithIndex((i,j)=>{
        val arrayBuffer=new ListBuffer[String]()
        var sum=0
        while (j.hasNext){
          sum+=j.next()
        }
        arrayBuffer+=("分区"+i+":和为"+sum)
        arrayBuffer.iterator
      })
    peopleDFwithIndex.foreach(println(_))

  }
}
