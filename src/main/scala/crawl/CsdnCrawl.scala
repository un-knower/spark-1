package crawl

import us.codecraft.webmagic.pipeline.{ConsolePipeline, FilePipeline, JsonFilePipeline}
import us.codecraft.webmagic.{Page, Site, Spider}
import us.codecraft.webmagic.processor.PageProcessor

class  CsdnProcessor extends  PageProcessor{

  //要查询的页面表达式
  val urlList=".*/forums/Java.*"
  //跳转的页面的详细表达式匹配
  val urlPost=".*/topics/\\d+"

  val site=Site.me().setDomain("bbs.csdn.net").setRetryTimes(5).setSleepTime(3000)
    //必须设置浏览器引擎
    .setUserAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36")

  //val site=Site.me().setRetryTimes(3).setSleepTime(10000)

  override def getSite: Site = {
    this.site
  }

  override def process(page: Page): Unit = {
    //利用url对列表和目标页分别进行处理
    //列表页
    if(page.getUrl.regex(urlList).`match`()){
      //必须是上一个节点的class ,将符合条件的url添加到抓取列表
      page.addTargetRequests(page.getHtml.xpath("//div[@class='content']").links().regex(urlPost).all())
      page.addTargetRequests(page.getHtml.xpath("//div[@class='page_nav']").links().regex(urlList).all())
    }else{
      //文章页
      //在跳转页面查找相关内容
      page.putField("标题", page.getHtml.xpath("//span[@class='title text_overflow']/text()").all())
      //截取表达式符合括号内的指定内容
     // page.putField("分数", page.getHtml.xpath("//div[@class='detail_title']/h1/span/text()").regex("\\[问题点数：(\\d+)分").all())
     // page.putField("时间", page.getHtml.xpath("//div[@class='detailed']//span[@class='time']/text()").regex("发表于：(.*)").all())

    }
  }
}

object CsdnCrawl {
  def main(args: Array[String]): Unit = {
    Spider.create(new CsdnProcessor)
      .addUrl("http://bbs.csdn.net/forums/Java")
      //.addPipeline(new JsonFilePipeline("D:\\webmagic"))
      .addPipeline(new ConsolePipeline)
      .thread(5)
      .run()
  }
}
