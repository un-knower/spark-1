package clworks

import org.apache.log4j.{Level, Logger}
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.linalg.distributed.{MatrixEntry, RowMatrix}
import org.apache.spark.{SparkConf, SparkContext}

object CosineSimilarity {

  def main(args: Array[String]): Unit = {

    //设置日志级别
    Logger.getLogger("org").setLevel(Level.ERROR)

    val conf = new SparkConf().setAppName("CosineSimilarity").setMaster("local[3]")
    val sc = new SparkContext(conf)
    val rows=sc.textFile("data/sample_svm_data.txt").map(line=>{
      val values=line.split(" ").map(_.toDouble)
      Vectors.dense(values)
    })

    //https://databricks.com/blog/2014/10/20/efficient-similarity-algorithm-now-in-spark-twitter.html
    val mat=new RowMatrix(rows)

    //322行17列矩阵
   // println(mat.numRows()+"--"+mat.numCols())

    // with brute force，返回原矩阵第x列和第y列的相似度。
    val exact=mat.columnSimilarities()

    //设定阈值，减少计算量，using DIMSUM
    val approx = mat.columnSimilarities(0.1)

    //exact.entries.foreach(println(_))
    val exactEntries = exact.entries.map { case MatrixEntry(i, j, u) => ((i, j), u) }
    val approxEntries = approx.entries.map { case MatrixEntry(i, j, v) => ((i, j), v) }

    /**
      * 1.((10,15),0.046423834544262986)
      * 2.((10,15),0.10010994681172564)
      */
    exactEntries.foreach(println(_))
    println("***********************")
    approxEntries.foreach(println(_))


//((3,9),(0.4517411826472004,Some(0.45397332592419104)))
    val MAE=exactEntries.leftOuterJoin(approxEntries).values.map{
  case (u,Some(v))=> math.abs(u - v)
  case (u,None)=> math.abs(u)
}.mean()
   // println(MAE)

  }
}
