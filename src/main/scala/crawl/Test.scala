package crawl

import org.jsoup.Jsoup
import us.codecraft.webmagic.pipeline.ConsolePipeline
import us.codecraft.webmagic.{Page, Site, Spider}
import us.codecraft.webmagic.processor.PageProcessor
import us.codecraft.xsoup.Xsoup

import scala.collection.JavaConversions._

class TestProcessor extends PageProcessor{
 // val site=Site.me().setDomain("www.qichacha.com").setRetryTimes(5).setSleepTime(3000)
   // .setUserAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36")

  val site = Site.me().setDomain("www.qichacha.com").setRetryTimes(10).setSleepTime(3000)
    .setUserAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.108 Safari/537.36")

     .addCookie("BAIDUID", "3195D64115E7B283E5F9E852F063F2DC:FG=1")
    // .addCookie("BCLID", "11631263402860303443")
     .addCookie("BDORZ", "B490B5EBF6F3CD402E515D22BCDA1598")
    // .addCookie("BDSFRCVID", "zmusJeC629VuAiQA8IDKUwov9eKaSgjTH6ao7YwnTzLcFH1vuUpnEG0PDf8g0Ku-JDhDogKK0eOTHk7P")
     .addCookie("BIDUPSID", "CC87B2AE6FDBD8CB01ADD3EBC89C0F37")
    .addCookie("CNZZDATA1254842228", "1861248724-1514364829-null%7C1514419987")
    //  .addCookie("H_BDCLCKID_SF", "fn-eVCIMfb_KH6rd-T--DKCShUFs-lTA-2Q-5KL--fKWKDT_Lj7b3xtA5Unqq4Qm2aCq_fbdJJjoMfb4j-Jk2bbQhtjXKPc-0eTxoUJ2QCnJhhvGqjAVLT-ebPRiJPQ9QgbW_MtLfIP5bDIxePKK-CCJ-N-qJI62aKDsLxTI-hcqEIL4hj7Oj4_XLH7fLtcPbKOqVR5CK-nAMxbSj4QoLRKn5Gb0Q5oJK5nX0nTFal5nhMJm257JDMP0qHAL35by523ion5vQpnOMUtuDT8WDjjyDaRf-b-XbKTKLJ5-HJOoDjrnh6R5Wh8yyxomtjjtaD7I-UjpfP34Sf56hnDBbfn03fT2LUkqKCOh0nba0tbvjqb6btQTyj_uQttjQnoOfIkja-KE5D_BSb7TyU42bf47yM4fQTIeJJ-e_CL5f-3bfTrlMtQ_5bLjMqoO-4LXKKOLV-cEb4Okeq8CD6r45f_t0tLOBx3iQ6vyLDTHWbj6enc2y5jHhT-45UTnhf723e-DLt5G-nrpsIJMQ-DWbT8U5ecZ0PvRaKviaKJHBMb1jI3Me4bK-TrBjauetfK")
      .addCookie("H_PS_PSSID", "25395_2453_1463_12897_21094_17001_25178")
    .addCookie("PHPSESSID", "voabit1lht809rgam2u22p62m0")
     .addCookie("PSINO", "1")
     .addCookie("PSTM", "1514369902")
     .addCookie("UM_distinctid", "160973e72cd4eb-0428a83c585d82-454f032b-1fa400-160973e72ce74f")
    // .addCookie("__cfduid", "d1b9c7a1f6bd6ff5f1277d50a9fa764571514337545")
    // .addCookie("__guid", "91251416.3626612098199937000.1514291406952.1013")
    // .addCookie("__huid", "11vVBNF+TBzLCWCnQ4BAgrlCHFE0ZbKbxRwjMNNfEyvYA=")
    .addCookie("_uab_collina", "151436594011342307895147")
    .addCookie("_umdata", "2BA477700510A7DF6FBA4516C66A3094A9C8ADAF0EC85F9E7072719BE343D9873441245D20C794B3CD43AD3E795C914C2C61616BC2DEA5FEB3B83E4D68DC8E69")
    //.addCookie("acw_sc__", "5a41f8d7d6e496f7822a2484d7d9421f19b4ae8a")
    .addCookie("acw_tc", "AQAAALO6ll6iwAEAzkbCt2CFhw60NN3G")
    .addCookie("hasShow", "1")
    //.addCookie("atpsida", "ce9e53c6087f07bba8848874_1514288070_9")
    // .addCookie("cna", "2GCcEupxl3UCAbfCRs69QNHA")
    .addCookie("zg_de1d1a35bfa24ce29bbf2c7eb17e6c4f", "%7B%22sid%22%3A%201514341421392%2C%22updated%22%3A%201514344960605%2C%22info%22%3A%201514273013555%2C%22superProperty%22%3A%20%22%7B%7D%22%2C%22platform%22%3A%20%22%7B%7D%22%2C%22utm%22%3A%20%22%7B%7D%22%2C%22referrerDomain%22%3A%20%22www.qichacha.com%22%2C%22cuid%22%3A%20%22caa4b02a57cec47c49a8839e95cc1241%22%7D")
    .addCookie("zg_did", "%7B%22did%22%3A%20%2216091b47f2b314-0330c80cdd2d0e-454f032b-1fa400-16091b47f2c660%22%7D")

    .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.108 Safari/537.36")
    .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
    //.addHeader("Accept", "text/html, */*; q=0.01")
    .addHeader("Accept-Encoding", "gzip, deflate")
    .addHeader("Accept-Language", "zh-CN,zh;q=0.9")
    .addHeader("Connection", "keep-alive")

  override def getSite: Site = {
    this.site
  }
  override def process(page: Page): Unit = {
    //page.putField("省份", page.getHtml.xpath("//*[@id=\"newAlexa\"]/table/tbody/tr/td").all())
    //links才能获取完整地址
    //page.putField("地址", page.getHtml.xpath("//*[@id=\"newAlexa\"]/table/tbody").links().all())
   // page.putField("second",page.getHtml().xpath("//*[@id=\"Cominfo\"]/table/tbody/tr[2]/td[1]/div/div[2]/a[1]/text()"))

   // val doc=Jsoup.connect(page.getUrl.get()).get()

    //page.putField("COMPANY_NAME",page.getHtml.xpath("//*[@id=\"searchlist\"]/table/tbody/tr[1]/td[2]/a/em/em/text()").get())
    //
    // page.putField("MOBILE",page.getHtml.xpath("//*[@id=\"searchlist\"]/table/tbody/tr[1]/td[2]/p[2]/text()/text()").regex("电话：(.*)").get())

//    page.putField("COMPANY_NAME", page.getHtml.xpath("//*[@id=\"company-top\"]/div/div[2]/div[1]/text()"))
//    page.putField("MOBILE",page.getHtml.xpath("//span[@class=\"cvlu\"]/span/text()"))
//    page.putField("EMAIL",page.getHtml.xpath("//*[@id=\"company-top\"]/div/div[2]/div[3]/span[4]/a/text()"))

    //page.putField("COMPANY_NAME",page.getHtml.xpath("//*[@id=\"company-top\"]/div/div[2]/div[1]/text()").get())
    //page.putField("REPRESENTATION",Xsoup.compile("//*[@id=\"Cominfo\"]/table/tbody/tr[2]/td[1]/div/div[2]/a[1]/text()").evaluate(doc).get())
    //page.putField("FOUND_DATE",Xsoup.compile("//*[@id=\"Cominfo\"]/table/tbody/tr[3]/td[4]/text()").evaluate(doc).get())
    //page.putField("REGISTER_CAPITAL",Xsoup.compile("//*[@id=\"Cominfo\"]/table/tbody/tr[3]/td[2]/text()").evaluate(doc).get())
    //page.putField("COMPANY_ADDRESS",page.getHtml.xpath("//*[@id=\"mapPreview\"]/text()").get())
    //page.putField("EMAIL",page.getHtml.xpath("//*[@id=\"company-top\"]/div/div[2]/div[3]/span[4]/a/text()").get())
    //page.putField("MOBILE",page.getHtml.xpath("//span[@class=\"cvlu\"]/span/text()").get())
    //page.putField("MANAGER_BUSINESS",Xsoup.compile("//*[@id=\"Cominfo\"]/table/tbody/tr[12]/td[2]/text()").evaluate(doc).get())
    //page.putField("OFFICIAL_SITE",page.getHtml.xpath("//*[@id=\"company-top\"]/div/div[2]/div[3]/span[2]/a/text()").get())


//    val infos=page.getHtml.xpath("//*[@id=\"searchlist\"]/table/tbody/tr/td[2]").all()
//    for(i<- 0 to infos.size()-1){
//
//    }

    page.putField("ALL", page.getHtml.xpath("//*[@id=\"searchlist\"]/table/tbody/tr/td"))

  }
}

object Test {
  def main(args: Array[String]): Unit = {


    Spider.create(new TestProcessor)
      .addUrl("http://www.qichacha.com/search?key=%E6%96%B0%E9%9B%B6%E5%94%AE#index:12&p:1&")
      .addPipeline(new ConsolePipeline)
      .thread(5)
      .run()


  }
}
