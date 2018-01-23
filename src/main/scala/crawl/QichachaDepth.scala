package crawl

import java.net.URLEncoder

import org.jsoup.Jsoup
import us.codecraft.webmagic.pipeline.ConsolePipeline
import us.codecraft.webmagic.{Page, Request, Site, Spider}
import us.codecraft.webmagic.processor.PageProcessor
import us.codecraft.xsoup.Xsoup

import scala.collection.JavaConversions._


class QichachaDepth extends PageProcessor{
  val keyUrl = "http://www\\.qichacha\\.com/search_index\\?key=.*&ajaxflag=1&p=\\d+&"
  val redicrectUrl = ".*/firm_\\w+\\.html$"
  val tagUrl = ".*#base"
  //val tagUrl = ".*#(base|susong|run|touzi|report|assets|#history)"

  val site = Site.me().setDomain("www.qichacha.com").setRetryTimes(10).setSleepTime(3000)
    .addCookie("BAIDUID", "15031658129327F10406562264A9EF27:FG=1")
    .addCookie("BDORZ", "B490B5EBF6F3CD402E515D22BCDA1598")
    .addCookie("BIDUPSID", "15031658129327F10406562264A9EF27")
    .addCookie("CNZZDATA1254842228", "1711716221-1513668380-null%7C1513668380")
    .addCookie("PHPSESSID", "jj35oac9oi594nh33l336ebfj1")
    .addCookie("PSINO", "1")
    .addCookie("PSTM", "1513672374")
    .addCookie("UM_distinctid", "1606dde49b0298-04901de09981a5-61131b7e-1fa400-1606dde49b1828")

    //登录信息1
    .addCookie("_uab_collina", "151367177254545304874935")
    //登录信息2
    .addCookie("_umdata", "486B7B12C6AA95F2EC7BBEDF64C615CF23617B695451F8D9A43263CDF12DFA63552156FA293E0B1DCD43AD3E795C914CFBE2D4D7AC75B1DFBE1A2F8232AF528A")

    .addCookie("acw_sc__", "5a38cc567dab0451d6d7b6093b6711987300b67d")
    .addCookie("acw_tc", "AQAAAIVUEFZdLgwAzkbCt7B0vYWxoIwT")
    .addCookie("hasShow", "1")
    .addCookie("pgv_pvi", "6812948480")
    .addCookie("pgv_si", "s8629057536")
    .addCookie("zg_de1d1a35bfa24ce29bbf2c7eb17e6c4f", "%7B%22sid%22%3A%201513315499556%2C%22updated%22%3A%201513317552652%2C%22info%22%3A%201513299794213%2C%22superProperty%22%3A%20%22%7B%7D%22%2C%22platform%22%3A%20%22%7B%7D%22%2C%22utm%22%3A%20%22%7B%7D%22%2C%22referrerDomain%22%3A%20%22%22%2C%22cuid%22%3A%20%2273cf842bbd4fece4e2ad0204908269ae%22%7D")
    .addCookie("zg_did", "%7B%22did%22%3A%20%2216057b25920177-0190470a213aa6-61131b7e-1fa400-16057b25921791%22%7D")

    .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.84 Safari/537.36")
    .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
    .addHeader("Accept-Encoding", "gzip, deflate")
    .addHeader("Accept-Language", "zh-CN,zh;q=0.9")
    .addHeader("Connection", "keep-alive")

   def getSite: Site = {
    this.site
  }


//  override def process(page: Page): Unit = {

//    if (page.getUrl.regex(keyUrl).`match`()) {
  //    page.addTargetRequests(page.getHtml.xpath("//table[@class='m_srchList']").links().regex(redicrectUrl).all())

    //}else if(page.getUrl.regex(redicrectUrl).`match`()){
      //page.addTargetRequests(page.getHtml.xpath("//*[@id=\"company-nav\"]/ul").links().regex(tagUrl).all())
    //} else {

      //page.putField("COMPANY_NAME",page.getHtml.xpath("//*[@id=\"company-top\"]/div/div[2]/div[1]/text()").get())
      //page.putField("MOBILE",page.getHtml.xpath("//span[@class=\"cvlu\"]/span/text()").get())
      //page.putField("ADDRESS",page.getHtml.xpath("//*[@id=\"mapPreview\"]/text()").get())

    //}
  //}


  /**
    * 在不同级的页面的不同处理,使用正则匹配，if
    * @param page
    */
  override def process(page: Page): Unit = {
    if(page.getUrl.regex(keyUrl).`match`()){
      val firstUrl=page.getHtml().xpath("//*[@id=\"searchlist\"]/table/tbody/tr/td/a").links().all()
      page.addTargetRequests(firstUrl)
    }else if(page.getUrl.regex(redicrectUrl).`match`()){
      val secondUrl=page.getHtml().xpath("//*[@id=\"company-nav\"]/ul/li/a").links().regex(tagUrl).all()
      page.addTargetRequests(secondUrl)
    }else {

      //当不能从原网站解析时，可以考虑使用Xsoup解析

      val doc=Jsoup.connect(page.getUrl.get()).get()
      //分别对应当前页面的xpath
      val daibiao=Xsoup.compile("//*[@id=\"Cominfo\"]/table/tbody/tr[2]/td[1]/div/div[2]/a[1]/text()").evaluate(doc).get()
      val ziben=Xsoup.compile("//*[@id=\"Cominfo\"]/table/tbody/tr[3]/td[2]/text()").evaluate(doc).get()
      //page.putField("COMPANY_NAME",page.getHtml.xpath("//*[@id=\"company-top\"]/div/div[2]/div[1]/text()"))
      // page.putField("代表人",page.getHtml.xpath("//*[@id=\"Cominfo\"]/table/tbody/tr[2]/td[1]/div/div[2]/a[1]/text()").get())
      page.putField("代表",daibiao)
     // page.putField("资本",ziben)
     // page.putField("MOBILE",page.getHtml.xpath("//span[@class=\"cvlu\"]/span/text()").get())

      //page.putField("资本",page.getHtml.xpath("//*[@id=\"Cominfo\"]/table/tbody/tr[3]/td[2]/text()"))

    }
  }


}

object QichachaDepth {
  def main(args: Array[String]): Unit = {
    val key="京东"
    val urlKey=URLEncoder.encode(key,"UTF-8")
    Spider.create(new QichachaDepth)
      .addUrl(s"http://www.qichacha.com/search_index?key=${urlKey}&ajaxflag=1&p=1&")
      .addPipeline(new ConsolePipeline)
      .thread(10)
      .run()
  }
}
