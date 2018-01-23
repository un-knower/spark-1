package crawl

import java.util.regex.Pattern

import org.apache.commons.lang3.StringUtils
import org.jsoup.Jsoup
import us.codecraft.webmagic._
import us.codecraft.webmagic.processor.PageProcessor
import us.codecraft.webmagic.scheduler.PriorityScheduler
import us.codecraft.xsoup.Xsoup

import scala.collection.JavaConversions._

/**
  * 层级关系及上下文信息。抓取n层的爬虫
  */
class DepthProcessor extends PageProcessor{

  val site=Site.me().setCharset("gb2312").setSleepTime(1000)

  override def getSite: Site = site

  //第一层级链接
  def processCountry(page: Page) = {
    //先取出td中包含的所有省份和地址
    val provinces=page.getHtml.xpath("//*[@id=\"newAlexa\"]/table/tbody/tr/td/a").all()
    for(i<- 0 to provinces.size()-1){
      val doc=Jsoup.parse(provinces(i))
      //获取链接
      val links=Xsoup.compile("//a//@href").evaluate(doc).get()
      //获取省份
      val title=Xsoup.compile("//a/text()").evaluate(doc).get()
      /**
        * Request保存待抓取url的对象
        * setPriority:设置优先级
        * putExtra:附加额外信息，额外信息会带到下次页面抓取中去
        * 含义：request添加待抓取的url和为下个页面添加的额外信息
        */
      val request=new Request(links).setPriority(0).putExtra("province",title)
      page.addTargetRequest(request)
    }
  }

  //第二层级链接
  def processProvince(page: Page)={
    val districts =page.getHtml().xpath("//body/table[4]/tbody/tr/td").all()
    for (i<- 0 to districts.size()-1){
      val doc=Jsoup.parse(districts(i))
      val links=Xsoup.compile("//a//@href").evaluate(doc).get()
      val title=Xsoup.compile("//a/b/text()").evaluate(doc).get()
      val request=new Request(links).setPriority(1).putExtra("province", page.getRequest().getExtra("province")).putExtra("district", title)
      page.addTargetRequest(request)
    }
  }

  //第三层级链接
  def processDistrict(page: Page)={
    val province=page.getRequest().getExtra("province").toString()
    val district = page.getRequest().getExtra("district").toString()
    val zipcode=page.getHtml().regex("<h2><div>邮编：(\\d+)</div></h2>").toString()
    page.putField("result",StringUtils.join(Array(province,district,zipcode),"\t"))
    val links=page.getHtml().links().regex("http://www\\.ip138\\.com/\\d{6}[/]?$").all()
    for(i<- 0 to links.size()-1){
      page.addTargetRequest(new Request(links(i)).setPriority(2).putExtra("province", province).putExtra("district", district))
    }
  }

  override def process(page: Page): Unit = {
    if(page.getUrl.toString.equals("http://www.ip138.com/post/")){
      processCountry(page)
    }else if(page.getUrl().regex("http://www\\.ip138\\.com/\\d{6}[/]?$").toString() != null){
      processDistrict(page)
    }else{
      processProvince(page)
    }
  }
}

object DepthCrawl {
  def main(args: Array[String]): Unit = {
    val scheduler=new PriorityScheduler
    Spider.create(new DepthProcessor)
      .setScheduler(scheduler)
      .addUrl("http://www.ip138.com/post/")
      .run()
  }
}
