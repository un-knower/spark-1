package crawl

import java.net.{URLDecoder, URLEncoder}
import java.util.Properties

import org.apache.spark.sql.SparkSession
import us.codecraft.webmagic.pipeline.{ConsolePipeline, Pipeline}
import us.codecraft.webmagic._
import us.codecraft.webmagic.processor.PageProcessor

class QichachaProcessor extends PageProcessor{

  val site=Site.me().setDomain("http://www.qichacha.com").setRetryTimes(5).setSleepTime(3000)
    .setUserAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36")

  val keyUrl="http://www\\.qichacha\\.com/search\\?key=.*"
  val redicrectUrl=".*/firm_\\w+\\.html"


  override def getSite: Site = {
    this.site
  }

  override def process(page: Page): Unit = {
    if(page.getUrl.regex(keyUrl).`match`()){
      page.addTargetRequests(page.getHtml.xpath("//table[@class='m_srchList']").links().regex(redicrectUrl).all())
    }else{
      page.putField("公司名字",page.getHtml.xpath("//*[@id=\"company-top\"]/div/div[2]/div[1]/text()"))
      page.putField("地址",page.getHtml.xpath("//*[@id=\"mapPreview\"]/text()"))
      page.putField("电话",page.getHtml.xpath("//*[@id=\"company-top\"]/div/div[2]/div[2]/span[2]/span/text()"))
      page.putField("代表人",page.getHtml.xpath("//*[@id=\"Cominfo\"]/table/tbody/tr[2]/td[1]/div/div[2]/a[1]/text()"))

    }
  }
}

class QichachaPipeline extends Pipeline{

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
    /*if(resultItems.get("title") != null & resultItems.get("date") != null){
      val df=ss.sparkContext.parallelize(List((resultItems.get("title").toString,resultItems.get("date").toString))).toDF()
      df.write.mode("append").jdbc(url,"CRAWL",properties)
    }*/
    //避免检查null
    val list=List((Option(resultItems.get("title")).getOrElse("0"),Option(resultItems.get("date")).getOrElse("1")))
    val df=ss.sparkContext.parallelize(list).toDF()
    df.write.mode("append").jdbc(url,"CRAWL",properties)
  }
}

object QichachaCrawl {
  def main(args: Array[String]): Unit = {

/*    val keywords=Array("上海创蓝文化传播有限公司","京东","百度")
    for(i<- 0 to keywords.length-1){
      val urlKey=URLEncoder.encode(keywords(i),"UTF-8")
      Spider.create(new QichachaProcessor)
        .addUrl(s"http://www.qichacha.com/search?key=${urlKey}")
        .addPipeline(new ConsolePipeline)
        .thread(10)
        .run()
    }*/


    //url地址的编码和解码
    println(URLDecoder.decode("%E4%BA%AC%E4%B8%9C","UTF-8"))
    //println(URLEncoder.encode("%25E4%25BA%25AC%25E4%25B8%259C","UTF-8"))

  }
}
