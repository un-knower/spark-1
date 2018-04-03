///**/
//
///*
//package crawl
//
//import java.net.URLEncoder
//import java.util.Properties
//
//import us.codecraft.webmagic.pipeline.{ConsolePipeline, JsonFilePipeline, Pipeline}
//import us.codecraft.webmagic._
//import us.codecraft.webmagic.processor.PageProcessor
//
//import scala.collection.JavaConversions._
//import scala.io.Source
//
//
//class QichachaSimProcessor extends PageProcessor{
//
//
//  val site = Site.me().setDomain("www.qichacha.com").setRetryTimes(10).setSleepTime(3000)
//    .addCookie("BAIDUID", "2534DBA9C979E8D15F99569C979AC1A6:FG=1")
//    .addCookie("BCLID", "13456075787484155636")
//    .addCookie("BDORZ", "B490B5EBF6F3CD402E515D22BCDA1598")
//    .addCookie("BIDUPSID", "2534DBA9C979E8D15F99569C979AC1A6")
//    .addCookie("CNZZDATA1254842228", "1558219253-1514270298-https%253A%252F%252Fwww.baidu.com%252F%7C1514270298")
//    .addCookie("H_BDCLCKID_SF", "tRk8oI-XJCvjD4-k247Hhn8thmT22-usQTLjQhcH0hOWsIO6XJ3_Ljkd2boZKPrR5jcmLbcH3tt5eDbxDUC0j63-jNt8qb3HKC7-sDKh2J7DKROvhj4BQj0yyxomtjjxtJ6aXxopfpOSKf56hnDBbfP_KNQ2LUkqKCOx2JrgJf3sh-LxjMRx-J8gQttjQn3PfIkja-KEBMjP8b7TyU42bf47yM4fQTT2-DA_oKtytDOP")
//    .addCookie("H_PS_PSSID", "1450_21106_25438_25178_22074")
//    .addCookie("PHPSESSID", "plgp0sd1hjpf0a2sphve8jgsp2")
//    .addCookie("PSINO", "1")
//    .addCookie("PSTM", "1514273005")
//    .addCookie("UM_distinctid", "16091b47f058da-0be225faa70bc6-454f032b-1fa400-16091b47f067de")
//    .addCookie("_uab_collina", "151427301534731182989287")
//    .addCookie("_umdata", "6AF5B463492A874DF24B34D8E0B24F55F973F962C65AA656743132B6D5756794E44D6FC326E04217CD43AD3E795C914C83C93A970C60051D6948F8FEDDB3ACD3")
//    .addCookie("acw_sc__", "5a41f8d7d6e496f7822a2484d7d9421f19b4ae8a")
//    .addCookie("acw_tc", "AQAAAKwjSEeAlAgAzkbCt8tw+j/caiqp")
//    .addCookie("hasShow", "1")
//    .addCookie("zg_de1d1a35bfa24ce29bbf2c7eb17e6c4f", "%7B%22sid%22%3A%201514273013553%2C%22updated%22%3A%201514273063713%2C%22info%22%3A%201514273013555%2C%22superProperty%22%3A%20%22%7B%7D%22%2C%22platform%22%3A%20%22%7B%7D%22%2C%22utm%22%3A%20%22%7B%7D%22%2C%22referrerDomain%22%3A%20%22www.baidu.com%22%2C%22cuid%22%3A%20%22e74909fc7c1382add51312dd7a1eaabb%22%7D")
//    .addCookie("zg_did", "%7B%22did%22%3A%20%2216091b47f2b314-0330c80cdd2d0e-454f032b-1fa400-16091b47f2c660%22%7D")
//
//    .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.84 Safari/537.36")
//    .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
//    .addHeader("Accept-Encoding", "gzip, deflate")
//    .addHeader("Accept-Language", "zh-CN,zh;q=0.9")
//    .addHeader("Connection", "keep-alive")
//
//  override def getSite: Site = {
//    this.site
//  }
//
//  override def process(page: Page): Unit = {
//    page.putField("COMPANY_NAME",page.getHtml.xpath("//*[@id=\"searchlist\"]/table/tbody/tr[1]/td[2]/a/em/em/text()").get())
//    page.putField("MOBILE",page.getHtml.xpath("//*[@id=\"searchlist\"]/table/tbody/tr[1]/td[2]/p[2]/text()/text()").regex("电话：(.*)").get())
//    page.putField("EMAIL",page.getHtml.xpath("//*[@id=\"searchlist\"]/table/tbody/tr[1]/td[2]/p[2]/span/text()").regex("邮箱：(.*)").get())
//    page.putField("REGISTER_CAPITAL",page.getHtml.xpath("//*[@id=\"searchlist\"]/table/tbody/tr[1]/td[2]/p[1]/span[1]/text()").regex("注册资本：(.*)").get())
//    page.putField("FOUND_DATE",page.getHtml.xpath("//*[@id=\"searchlist\"]/table/tbody/tr[1]/td[2]/p[1]/span[2]/text()").regex("成立时间：(.*)").get())
//  }
//
//}
//
//class QichachaSimPipeline  extends Pipeline {
//
//  import Finals.hiveContext.implicits._
//
//
//  Class.forName("oracle.jdbc.driver.OracleDriver")
//  val url="jdbc:oracle:thin:@192.168.0.194:1521:smsdb"
//  val user="smsdb"
//  val password="chuanglan789"
//  val driver="oracle.jdbc.driver.OracleDriver"
//  val properties=new Properties()
//  properties.put("user",user)
//  properties.put("password",password)
//  properties.put("driver",driver)
//
//  override def process(resultItems: ResultItems, task: Task): Unit = {
//
//    val list=List((Option(resultItems.get("COMPANY_NAME")).getOrElse("0"),Option(resultItems.get("MOBILE")).getOrElse("0"),Option(resultItems.get("EMAIL")).getOrElse("0"),
//      Option(resultItems.get("REGISTER_CAPITAL")).getOrElse("0"),Option(resultItems.get("FOUND_DATE")).getOrElse("0")))
//    val df=Finals.sc.parallelize(list).toDF("COMPANY_NAME","MOBILE","EMAIL","REGISTER_CAPITAL","FOUND_DATE").distinct()
//    df.write.mode("append").jdbc(url,"Qichacha_SimCrawl",properties)
//
//  }
//}
//
//
//
//
//
//    //写入oracle很慢
//
///*    Class.forName("oracle.jdbc.driver.OracleDriver")
//    val url="jdbc:oracle:thin:@192.168.0.194:1521:smsdb"
//    val user="smsdb"
//    val password="chuanglan789"
//    val driver="oracle.jdbc.driver.OracleDriver"
//    val properties=new Properties()
//    properties.put("user",user)
//    properties.put("password",password)
//    properties.put("driver",driver)
//    val oriDF=Finals.hiveContext.read.jdbc(url,"COMPANY_NAME",properties).select("COMPANY_NAME").cache()
//    val companys=oriDF.collectAsList()
//    for(i<- 0 to companys.size()-1) {
//      val company = companys(i).toString().substring(companys(i).toString().indexOf("[") + 1, companys(i).toString().indexOf("]"))
//      val urlKey = URLEncoder.encode(company, "UTF-8")
//      Spider.create(new QichachaSimProcessor)
//        .addUrl(s"http://www.qichacha.com/search?key=${urlKey}")
//        .addPipeline(new QichachaSimPipeline)
//        .thread(10)
//        .run()
//    }*/
//
//
//
//
//  }
//}
//*/
//*/
//*/
