package crawl

import us.codecraft.webmagic.{Page, Site, Spider}
import us.codecraft.webmagic.pipeline.ConsolePipeline
import us.codecraft.webmagic.processor.PageProcessor

class  BokeyuanProcessor extends PageProcessor{

  val site=Site.me().setDomain("www.cnblogs.com").setRetryTimes(5).setSleepTime(3000)
    .setUserAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36")

  val listUrl=".*/sitehome/p/\\d+"
  val contentUrl="http://www\\.cnblogs\\.com/\\w+/p/[0-9]{7}\\.html"

  override def getSite: Site = {
    this.site
  }

  override def process(page: Page): Unit = {
    /*
    从单个内容页面找出所有标题
    if(!page.getUrl.regex(contentUrl).`match`()){
      page.addTargetRequests(page.getHtml.xpath("//div[@class='post_item_body']").links().regex(contentUrl).all())
     // page.addTargetRequests(page.getHtml.xpath("//div[@class='paper']").links().regex(listUrl).all())
    }else{
      page.putField("标题",page.getHtml.xpath("//h1[@class='postTitle']/a/text()"))
    }*/

    if(page.getUrl.regex(listUrl).`match`()){
      //跳转的页面url
      page.addTargetRequests(page.getHtml.xpath("//div[@class='post_item_body']").links().regex(contentUrl).all())
      //当前页面url
      page.addTargetRequests(page.getHtml.xpath("//div[@class='pager']").links().regex(listUrl).all())
    }else{
      page.putField("标题",page.getHtml.xpath("//h1[@class='postTitle']/a/text()"))
    }


  }
}

object BokeyuanCrawl {
  def main(args: Array[String]): Unit = {
    Spider.create(new BokeyuanProcessor)
      //初始url匹配url列表
      .addUrl("https://www.cnblogs.com/sitehome/p/1")
      .addPipeline(new ConsolePipeline)
      .thread(10)
      .run()
  }
}
