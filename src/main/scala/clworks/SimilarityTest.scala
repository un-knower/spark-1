package clworks

import com.github.fommil.netlib.F2jBLAS
import com.github.fommil.netlib.{BLAS => NetlibBLAS, F2jBLAS}
import com.github.fommil.netlib.BLAS.{getInstance => NativeBLAS}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.mllib.linalg.{DenseVector, SparseVector, Vector, Vectors}

import scala.collection.mutable.Map
import scala.collection.JavaConversions._


object SimilarityTest {
  def main(args: Array[String]): Unit = {


    val user1=Map("张三" ->Map("逆战" -> 2, "人间" -> 3, "鬼屋" -> 1, "西游记" -> 0, "雪豹" -> 1))
    val user2=Map("李四" ->Map("逆战" -> 1, "人间" -> 2, "鬼屋" -> 2, "西游记" -> 1, "雪豹" -> 4))

    /**
      *
      */

    //得到用户的评分值
    //HashMap(1, 0, 2, 1, 3)
/*    println(user1.get("张三").get.values)
    println(user1.get("张三").get.values.toVector)*/

    //获取两个人的评分
    val user1FilmSource = user1.get("张三").get.values.toVector
    val user2FilmSource = user2.get("李四").get.values.toVector

    //println(user1FilmSource)

    //计算分子
    //Vector((1,2), (0,1), (2,1), (1,4), (3,2))
    //Vector(2, 0, 2, 4, 6)
    //14.0
/*    println(user1FilmSource.zip(user2FilmSource))
    println(user1FilmSource.zip(user2FilmSource).map(i=>i._1*i._2))*/
    val fenzi=user1FilmSource.zip(user2FilmSource).map(i=>i._1*i._2).reduce(_+_).toDouble


    //计算分母
    val fenmu1=math.sqrt(user1FilmSource.map(i=>math.pow(i,2)).reduce(_+_))
    val fenmu2=math.sqrt(user2FilmSource.map(i=>math.pow(i,2)).reduce(_+_))

    //计算余弦相似度  0.7089175569585667
    //println(fenzi/(fenmu1*fenmu2))

    val user1Vec = user1.get("张三").get.values.map(_.toDouble).toArray
    val user2Vec = user2.get("李四").get.values.map(_.toDouble).toArray

    //转换为向量
    val dv1=Vectors.dense(user1Vec).toDense
    val dv2=Vectors.dense(user2Vec).toDense

    val f2jBLAS=new F2jBLAS

    //计算余弦相似度的分子
    def dot(x: DenseVector, y: DenseVector): Double = {
      val n = x.size
      f2jBLAS.ddot(n, x.values, 1, y.values, 1)
    }
    val first=dot(dv1,dv2)

    //计算余弦相似度的分母
    val v1=Vectors.norm(dv1,2.0)
    val v2=Vectors.norm(dv2,2.0)
    val second=v1*v2

    //计算余弦相似度  0.7089175569585667
    println(first/second+"---------------")

    //println(Utils.cosineSimilarity(dv1,dv2,f2jBLAS))
  }
}
