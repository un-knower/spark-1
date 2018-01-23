package crawl

import org.jsoup.Jsoup
import us.codecraft.xsoup.Xsoup

import scala.io.Source
import scala.collection.JavaConversions._
object JsoupTest {
  def main(args: Array[String]): Unit = {
//    val html="<td class=\"ma_left \" colspan=\"2\"> <div class=\"boss-td\"> <div class=\"pull-left\"> <img class=\"bheadimg\" src=\"/material/theme/chacha/cms/v2/images/boss_head.png\"> </div> <div class=\"pull-left\" style=\"max-width: 235px;\"> <a href=\"/pl_p8ae6f189808b7d13ab341ca8a349cfa.html\" class=\"bname\">王树清</a> <a class=\"btouzi\" href=\"/pl_p8ae6f189808b7d13ab341ca8a349cfa.html\">他关联 <span>1</span> 家公司 &gt; </a> </div> <div class=\"pull-right\"> <a href=\"javascript:;\" onclick=\"opercorDetail('王树清','p8ae6f189808b7d13ab341ca8a349cfa','1450cc312fde0479f07d810fe8b2e2f9')\" class=\"btn btn-primary\">查看人物图谱</a> </div> </div> </td>"
//
//    //解析url
//    val doc=Jsoup.connect("http://www.qichacha.com/firm_1450cc312fde0479f07d810fe8b2e2f9.html#base").get()
//
//    //为空值原因：解析出的html变了
//    //println(doc.html())
//    //解析不存在的xpath返回null
//    val result=Xsoup.compile("//*[@id=\"Cominfo\"]/table/tbody/tr[3]/td[2]/text()").evaluate(doc).get()
//    //println(result)
//
//    val file=Source.fromFile("data/cookies.txt").getLines()
//    while(file.hasNext){
//      println(file.next())
//    }


/*    val h=""
    val d=Jsoup.parseBodyFragment(h)
    println(d)*/

    //val doc=Jsoup.connect("http://www.xicidaili.com/nn/1").get()


    /**
      * 循环遍历div下的所有tr中的td内容
      */
      val h="<ul class=\"odd\"> \n <li class=\"counuly\"><img src=\"http://fs.xicidaili.com/images/flag/cn.png\" alt=\"Cn\"></li> \n <li>61.135.217.7</li> \n <li>80</li> \n <li> <a href=\"/2016-05-13/beijing\">北京</a> </li> \n <li class=\"counuly\">高匿</li> \n <li>HTTP</li> \n <li class=\"counuly\"> \n  <div title=\"0.324秒\" class=\"bar\"> \n   <div class=\"bar_inner fast\" style=\"width:94%\"> \n   </div> \n  </div> </li> \n <li class=\"counuly\"> \n  <div title=\"0.064秒\" class=\"bar\"> \n   <div class=\"bar_inner fast\" style=\"width:99%\"> \n   </div> \n  </div> </li> \n <li>615天</li> \n <li>18-01-18 19:21</li> \n</ul>\n<ul class=\"\"> \n <li class=\"counuly\"><img src=\"http://fs.xicidaili.com/images/flag/cn.png\" alt=\"Cn\"></li> \n <li>122.114.31.177</li> \n <li>808</li> \n <li> <a href=\"/2017-11-27/henan\">河南郑州</a> </li> \n <li class=\"counuly\">高匿</li> \n <li>HTTP</li> \n <li class=\"counuly\"> \n  <div title=\"0.107秒\" class=\"bar\"> \n   <div class=\"bar_inner fast\" style=\"width:95%\"> \n   </div> \n  </div> </li> \n <li class=\"counuly\"> \n  <div title=\"0.021秒\" class=\"bar\"> \n   <div class=\"bar_inner fast\" style=\"width:96%\"> \n   </div> \n  </div> </li> \n <li>52天</li> \n <li>18-01-18 19:21</li> \n</ul>"
    val d=Jsoup.parseBodyFragment(h)
    val ip=List()
    val port=List()

    val r=Xsoup.compile("//ul/li[2]/text()").evaluate(d).list()
    val s=Xsoup.compile("//ul/li[3]/text()").evaluate(d).list()
/*    r.foreach(println(_))
    println("---------")
    s.foreach(println(_))*/

    val a=ip.::(r)
    val b=port.::(s)

    a.zip(b).foreach(println(_))

  }
}
