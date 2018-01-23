package crawl

import java.net.URLEncoder
import java.util.Properties

import org.apache.spark.sql.SparkSession
import us.codecraft.webmagic.pipeline.{ConsolePipeline, Pipeline}
import us.codecraft.webmagic._
import us.codecraft.webmagic.processor.PageProcessor

class QichachaProcessor_1 extends PageProcessor {


  val keyUrl = "http://www\\.qichacha\\.com/search_index\\?key=.*&ajaxflag=1&p=\\d+&"
  val redicrectUrl = ".*/firm_\\w+\\.html"
  val tagUrl = ".*#(base|susong|run|touzi|report|assets|#history)"

  val site = Site.me().setDomain("www.qichacha.com").setRetryTimes(10).setSleepTime(3000)
    .addCookie("BAIDUID", "04549F851C47A5779483D249EB1A74D5:FG=1")
    .addCookie("BDSFRCVID", "dqIsJeCCxG3watvA-NlyHbxpLsI4f1ethRla3J")
    .addCookie("BIDUPSID", "7DAE1EC3BFF044B01C4468166C9E19F1")
    .addCookie("CNZZDATA1254842228", "1648109519-1513298960-null%7C1513555328")
    .addCookie("H_BDCLCKID_SF", "tR-tVI-MfI03fP36qROE-tC--fT2aP_XKKOLVbbhW4OkeqOJ2Mt55P_354LLWt78WIrOQpRbWCbthJ5mMh5S-jtpexbH55uDJnCt_f5")
    .addCookie("H_PS_PSSID", "1433_21113_17001_25177_20929")
    .addCookie("PHPSESSID", "ih7tc94v5bim390v4bidv7eda2")
    .addCookie("PSINO", "1")
    .addCookie("PSTM", "1513300451")
    .addCookie("UM_distinctid", "16057b2588b64f-0db29154b17a31-61131b7e-1fa400-16057b2588c79b")

    //登录信息1
    .addCookie("_uab_collina", "151329994238894678636947")
    //登录信息2
    .addCookie("_umdata", "C234BF9D3AFA6FE788618F969D6D5F320DE8F5AF65BE37BC52431EE836397C0ABC5B2659C5986E7DCD43AD3E795C914C4FC9F22306F91F632C1A49C4E1F78B26")

    .addCookie("acw_tc", "AQAAAFs1+GrP/wEAzkbCtxQtMWHkwIcN")
    .addCookie("hasShow", "1")
    .addCookie("zg_de1d1a35bfa24ce29bbf2c7eb17e6c4f", "%7B%22sid%22%3A%201513315499556%2C%22updated%22%3A%201513317552652%2C%22info%22%3A%201513299794213%2C%22superProperty%22%3A%20%22%7B%7D%22%2C%22platform%22%3A%20%22%7B%7D%22%2C%22utm%22%3A%20%22%7B%7D%22%2C%22referrerDomain%22%3A%20%22%22%2C%22cuid%22%3A%20%2273cf842bbd4fece4e2ad0204908269ae%22%7D")
    .addCookie("zg_did", "%7B%22did%22%3A%20%2216057b25920177-0190470a213aa6-61131b7e-1fa400-16057b25921791%22%7D")

    .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.84 Safari/537.36")
    .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
    .addHeader("Accept-Encoding", "gzip, deflate")
    .addHeader("Accept-Language", "zh-CN,zh;q=0.9")
    .addHeader("Connection", "keep-alive")

  override def getSite: Site = {
    this.site
  }


  override def process(page: Page): Unit = {

    if (page.getUrl.regex(keyUrl).`match`()) {
      page.addTargetRequests(page.getHtml.xpath("//table[@class='m_srchList']").links().regex(redicrectUrl).all())

    } else {

      page.putField("COMPANY_NAME",page.getHtml.xpath("//*[@id=\"company-top\"]/div/div[2]/div[1]/text()").get())
      page.putField("MOBILE",page.getHtml.xpath("//span[@class=\"cvlu\"]/span/text()").get())
      page.putField("ADDRESS",page.getHtml.xpath("//*[@id=\"mapPreview\"]/text()").get())

    }
  }
}


class QichachaPipeline_1  extends Pipeline {

  val ss=SparkSession.builder().appName("crawl").master("local[3]")
    .getOrCreate()
  import ss.implicits._


  Class.forName("oracle.jdbc.driver.OracleDriver")
  val url="jdbc:oracle:thin:@192.168.0.194:1521:smsdb"
  val user="smsdb"
  val password="chuanglan789"
  val driver="oracle.jdbc.driver.OracleDriver"
  val properties=new Properties()
  properties.put("user",user)
  properties.put("password",password)
  properties.put("driver",driver)

  override def process(resultItems: ResultItems, task: Task): Unit = {

    val list=List((Option(resultItems.get("COMPANY_NAME")).getOrElse("0"),Option(resultItems.get("MOBILE")).getOrElse("0"),Option(resultItems.get("ADDRESS")).getOrElse("0")))
    val df=ss.sparkContext.parallelize(list).toDF("COMPANY_NAME","MOBILE","ADDRESS")
    df.write.mode("append").jdbc(url,"CRAWL_COMPANY",properties)

  }
}


object Qichacha_Info {
  def main(args: Array[String]): Unit = {
    val company=Array("京东","百度")
    for(i<- 0 to company.length-1){
      val urlKey=URLEncoder.encode(company(i),"UTF-8")
      Spider.create(new QichachaProcessor_1)
        .addUrl(s"http://www.qichacha.com/search_index?key=${urlKey}&ajaxflag=1&p=1&")
        .addPipeline(new QichachaPipeline_1)
        .thread(10)
        .run()
    }

  }
}
