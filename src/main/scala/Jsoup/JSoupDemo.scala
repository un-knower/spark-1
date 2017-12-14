package Jsoup

import java.io.File

import org.jsoup.Jsoup

object JSoupDemo {
  def main(args: Array[String]): Unit = {

    val html = "<html><head><title>First parse</title></head>"+
      "<body><p>Parsed HTML into a doc.</p></body></html>"
    /**
      * 1.解析html字符串,生成Document
      * 2.文档由多个Elements和TextNodes组成，Document继承Element继承Node. TextNode继承 Node.
      */
    val doc=Jsoup.parse(html)   //返回结构合理的文档，其中包含(至少) 一个head和一个body元素。
    //println(doc.html())

    //解析body，可以是片段
    val body="<div><p>Lorem ipsum.</p>"
    val bodyDoc=Jsoup.parseBodyFragment(body)
    println(bodyDoc.body())

    //从一个URL加载一个Document
    val urlDoc=Jsoup.connect("https://movie.douban.com/chart").get()

    //从指定文件加载
    val fileDoc=Jsoup.parse(new File("data/example.html"),"UTF-8")
    println(fileDoc.head())


  }
}
