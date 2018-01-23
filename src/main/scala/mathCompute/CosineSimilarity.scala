package mathCompute

import com.github.fommil.netlib.F2jBLAS
import org.apache.spark.mllib.linalg.{DenseVector, Vectors}
import org.apache.spark.sql.SparkSession

object CosineSimilarity {
  def main(args: Array[String]): Unit = {

    val ss=SparkSession.builder().appName("test").master("local[3]")
      .getOrCreate()
    import ss.implicits._

    //计算两个向量间的余弦相似度的两种方法

    /**
      * 1.使用数学公式
      */


    val user1=Map("张三" ->Map("逆战" -> 2, "人间" -> 3, "鬼屋" -> 1, "西游记" -> 0, "雪豹" -> 1))
    val user2=Map("李四" ->Map("逆战" -> 1, "人间" -> 2, "鬼屋" -> 2, "西游记" -> 1, "雪豹" -> 4))

    //得到用户的评分值  结果为HashMap(1, 0, 2, 1, 3)
    val mathNum1=user1.get("张三").get.values
    val mathNum2=user2.get("李四").get.values
    //计算分子
    val up=mathNum1.zip(mathNum2).map(i=>i._1*i._2).reduce(_+_).toDouble

    //计算分母
    val down1=math.sqrt(mathNum1.map(i=>math.pow(i,2)).reduce(_+_))
    val down2=math.sqrt(mathNum2.map(i=>math.pow(i,2)).reduce(_+_))

    //计算相似度   0.7089175569585667
    val cosineSimilarity=up/(down1*down2)
    println(cosineSimilarity)


    /**
      * 2.将文本转换为向量
      */
    val user1Vec = user1.get("张三").get.values.map(_.toDouble).toArray
    val user2Vec = user2.get("李四").get.values.map(_.toDouble).toArray
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
    //println(first/second)

    /**
      *
      */

    user1Vec.foreach(println(_))


  }
}
