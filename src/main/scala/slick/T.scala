package slick

import org.apache.spark.sql.SparkSession

object T {
  def main(args: Array[String]): Unit = {
    val ss=SparkSession.builder().appName("bayes").master("local[3]")
      .getOrCreate()
    import ss.implicits._
    val df1=ss.read.option("header","true").csv("data/labels.txt")
    val df2=df1.rdd.map(i=>(i(0).toString,i(1).toString)).reduceByKey((i,j)=>i+"-"+j).toDF("mobile","classify_type")
    df2.write.csv("C:\\Users\\ChuangLan\\Desktop\\xxq.csv")
  }
}
