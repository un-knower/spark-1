package crawl

import java.net.URLEncoder

import org.openqa.selenium.Cookie
import us.codecraft.webmagic.pipeline.ConsolePipeline
import us.codecraft.webmagic.{Page, Site, Spider}
import us.codecraft.webmagic.processor.PageProcessor

class CookieProcessor extends PageProcessor{

  val keyUrl="http://www\\.qichacha\\.com/search_index\\?key=.*&ajaxflag=1&p=\\d+&"
  val redicrectUrl=".*/firm_\\w+\\.html"

  //添加cookie之前一定要先设置域名，否则cookie信息不生效
  val site=Site.me().setDomain("www.qichacha.com").setRetryTimes(5).setSleepTime(3000)

    //添加所有登陆后获取的cookie信息，其中包含用户登录信息
    .addCookie("BAIDUID","04549F851C47A5779483D249EB1A74D5:FG=1")
    .addCookie("CNZZDATA1254842228","1648109519-1513298960-null%7C1513298960")
    .addCookie("PHPSESSID","ih7tc94v5bim390v4bidv7eda2")
    .addCookie("UM_distinctid","16057b2588b64f-0db29154b17a31-61131b7e-1fa400-16057b2588c79b")
    //登录信息1
    .addCookie("_uab_collina","151329994238894678636947")
    //登录信息2
    .addCookie("_umdata","C234BF9D3AFA6FE788618F969D6D5F320DE8F5AF65BE37BC52431EE836397C0ABC5B2659C5986E7DCD43AD3E795C914C4FC9F22306F91F632C1A49C4E1F78B26")

    .addCookie("acw_tc","AQAAAFs1+GrP/wEAzkbCtxQtMWHkwIcN")
    .addCookie("hasShow","1")
    .addCookie("zg_de1d1a35bfa24ce29bbf2c7eb17e6c4f","%7B%22sid%22%3A%201513299794211%2C%22updated%22%3A%201513299846696%2C%22info%22%3A%201513299794213%2C%22superProperty%22%3A%20%22%7B%7D%22%2C%22platform%22%3A%20%22%7B%7D%22%2C%22utm%22%3A%20%22%7B%7D%22%2C%22referrerDomain%22%3A%20%22www.qichacha.com%22%7D")
    .addCookie("zg_did","%7B%22did%22%3A%20%2216057b25920177-0190470a213aa6-61131b7e-1fa400-16057b25921791%22%7D")


    //添加请求头，有些网站会根据请求头判断该请求是由浏览器发起还是由爬虫发起的
    .addHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.84 Safari/537.36")
    .addHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
    .addHeader("Accept-Encoding","gzip, deflate")
    .addHeader("Accept-Language","zh-CN,zh;q=0.9")
    .addHeader("Connection","keep-alive")

  override def getSite: Site = {
    this.site
  }

  override def process(page: Page): Unit = {
    if(page.getUrl.regex(keyUrl).`match`()){
      page.addTargetRequests(page.getHtml.xpath("//table[@class='m_srchList']").links().regex(redicrectUrl).all())
    }else{
      page.putField("公司名字",page.getHtml.xpath("//*[@id=\"company-top\"]/div/div[2]/div[1]/text()"))
    }
  }
}

object CookieCrawl {
  def main(args: Array[String]): Unit = {
    val keywords="京东"
    val urlKey=URLEncoder.encode(keywords,"UTF-8")
    //当a标签中的href='javascript:getSearchPage(3,"1","")'格式时，添加的初始url必须根据network解析成原始url
    for(i<- 1 to 5){
      Spider.create(new CookieProcessor)
        .addUrl(s"http://www.qichacha.com/search_index?key=${urlKey}&ajaxflag=1&p=${i}&")
        .addPipeline(new ConsolePipeline)
        .thread(10)
        .run()
    }
  }
}
