package crawl

import us.codecraft.webmagic.pipeline.{ConsolePipeline, FilePipeline}
import us.codecraft.webmagic.{Page, Site, Spider}
import us.codecraft.webmagic.processor.PageProcessor

class XiaoshuoProcessor extends PageProcessor{

  val site=Site.me().setDomain("http://www.yangguiweihuo.com").setRetryTimes(5).setSleepTime(3000)
    .setUserAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36")

  val oriUrl="http://www\\.yangguiweihuo\\.com/paihang\\.html"
  val redictUrl="http://www\\.yangguiweihuo\\.com/[0-9]+/[0-9]+/"

  override def getSite: Site = {
    this.site
  }

  override def process(page: Page): Unit = {

    //单个列表页面跳转
/*
    page.putField("标题",page.getHtml.xpath("//div[@class='book_content_text']/h1/text()").all())
   // page.putField("内容",page.getHtml.xpath("//div[@id='book_text']/text()").all())

    page.addTargetRequests(page.getHtml.xpath("//div[@class='book_content_text_next']").links().regex(redictUrl).all())
*/

/*    if(page.getUrl.regex(oriUrl).`match`()){
      page.addTargetRequests(page.getHtml.xpath("//div[@class='book_content_text_next']").links().regex(redictUrl).all())
    }else{

    }*/

  }
}

object XiaoshuoCrawl {
  def main(args: Array[String]): Unit = {
    Spider.create(new XiaoshuoProcessor)
      .addUrl("http://www.yangguiweihuo.com/11/11516/6436503.html")
      .addPipeline(new ConsolePipeline)
      .thread(10)
      .run()
  }
}
