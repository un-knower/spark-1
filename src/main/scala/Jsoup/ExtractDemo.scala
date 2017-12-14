package Jsoup

import java.io.File

import org.jsoup.Jsoup
import scala.collection.JavaConversions._

object ExtractDemo {
  def main(args: Array[String]): Unit = {
    val fileDoc=Jsoup.parse(new File("data/example.html"),"UTF-8")

    //DOM语法：提取指定class的文件
    val content=fileDoc.getElementsByClass("nbg")
    for(i<-content){
     // println(i.attr("title"))
    }

    //选择器语法查找元素
    val titile=fileDoc.select("a.nbg")  //class等于nbg的a标签
    for(i<-titile){
      println(i.attr("title"))
    }



  }
}
