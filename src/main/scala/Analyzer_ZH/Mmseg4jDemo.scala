package Analyzer_ZH

import java.io.StringReader
import java.util

import com.chenlb.mmseg4j._

import scala.collection.mutable.{ArrayBuffer, ListBuffer}

object Mmseg4jDemo {
  def main(args: Array[String]): Unit = {
    val text="手机电子书    abc   http://www.sjshu.com"
    val dic=Dictionary.getInstance()
    val seg=new ComplexSeg(dic)
    val mmSeg=new MMSeg(new StringReader(text),seg)
    var word:Word=null

    while((word=mmSeg.next())!=null){
      val arr=new ArrayBuffer[String]()
      if(word!=null){
        val w=word.getString
        arr+=w+"->"
        arr.foreach(println(_))
      }

    }


    while((word=mmSeg.next())!=null){
      if(word!=null){
        println(word.getString+"|")
      }
    }
  }
}
