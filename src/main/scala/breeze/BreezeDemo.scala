package breeze

import breeze.linalg._
import breeze.plot._

object BreezeDemo {
  def main(args: Array[String]): Unit = {

    /**
      * 1.DenseVector或者SparseVector创建的所有向量都是列向量
      * 2.创建向量有多种方式
      * 3.Transpose[Vector[T]]创建行向量
      * 4.向量可通过索引访问，0 to x.length-1。也可从末尾访问(i<0)，x(i) == x(x.length + i)
      * 5.支持切片,使用range(0 to n)
      */

    /**
      *
      */

    val a=DenseVector.zeros[Double](5)
    val a1=DenseVector(1,2,3)
    //val a2=DenseVector.fill(5)(3)
   // val a3=DenseVector.tabulate(5)
    //:= 表示使用指定值替换原有向量的内容（可以使用具体值或者使用相同长度的向量代替）
    a1(0 to 1) := 5
    a1(0 to 1) := DenseVector(4,5)
    val b=SparseVector.zeros[Double](5)
   // println(a1)

    val c=DenseMatrix.zeros[Int](5,5)
    // transpose to match row shape，且必须具有相同的长度
   // c(4,::) := DenseVector(1,2,3,4).t

    //行、列必须跟指定的长度相等
    c(0 to 1,0 to 1) := DenseMatrix((1,2),(-1,-2))
    println(c)

    val csc=new CSCMatrix.Builder[Double](10,10)
    csc.add(3,4,1.0)
    println(csc.result)

    val counter=Counter("a"->1,"b"->2)
    println(counter.dot(counter))
  }
}
