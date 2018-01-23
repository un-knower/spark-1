package crawl.selenium

import java.sql.DriverManager
import java.util
import java.util.Properties
import java.util.concurrent.TimeUnit

import org.apache.http.impl.client.BasicCookieStore
import org.apache.http.impl.cookie.BasicClientCookie
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.hive.HiveContext
import org.apache.spark.{SparkConf, SparkContext}
import org.openqa.selenium.{By, Cookie}
import org.openqa.selenium.chrome.ChromeDriver
import us.codecraft.webmagic.pipeline.{ConsolePipeline, JsonFilePipeline, Pipeline}
import us.codecraft.webmagic.processor.PageProcessor
import us.codecraft.webmagic._

import scala.collection.JavaConversions._

class SjjySelenium extends  PageProcessor{
  val site=Site.me().setDomain("www.jiayuan.com").setRetryTimes(5).setSleepTime(3000)
    .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.84 Safari/537.36")
    .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
    .addHeader("Accept-Encoding", "gzip, deflate")
    .addHeader("Accept-Language", "zh-CN,zh;q=0.9")
    .addHeader("Connection", "keep-alive")



  val driver=new ChromeDriver()
  driver.manage().window().maximize()
  driver.manage.timeouts.implicitlyWait(10, TimeUnit.SECONDS)
  driver.get("http://login.jiayuan.com/")

  //必须填写正确的账户和密码，模拟登录
  driver.findElement(By.id("login_email")).clear()
  driver.findElement(By.id("login_email")).sendKeys("17321171953")
  driver.findElement(By.id("login_password")).clear()
  driver.findElement(By.id("login_password")).sendKeys("wzq123321")
  //driver.findElement(By.id("check_save")).click()
  driver.findElement(By.id("login_btn")).submit()

  /**
    * 添加该句的作用,因为网站不一定可以马上打开，让线程停一下，否则页面还没有加载出来就进行下一步了。
    * 强制当前正在执行的线程休眠（暂停执行），以“减慢线程”。当睡眠时间到期，则返回到可运行状态
    */
  Thread.sleep(5000)

  //获取cookies，site添加cookies
  val cookiesList=driver.manage().getCookies.toList

/*
val cookieStore=new BasicCookieStore

 for(i<- 0 to cookiesList.size-1){
    val bcco=new BasicClientCookie(cookiesList(i).getName,cookiesList(i).getValue)
    cookieStore.addCookie(bcco)
  }

  val allCookies=cookieStore.getCookies.toList
  for(i<- 0 to allCookies.size-1){
    site.addCookie(allCookies(i).getName,allCookies(i).getValue)
  }*/

  for(i<- 0 to cookiesList.size-1){
    site.addCookie(cookiesList(i).getName,cookiesList(i).getValue)
  }

  //driver.quit()

  override def process(page: Page): Unit = {
    page.putField("ID",page.getHtml.xpath("//div[@class='member_info_r yh']/h4/span/text()").regex("ID:(\\d+)"))
    //page.putField("基本信息",page.getHtml.xpath("//ul[@class='member_info_list fn-clear']//li/div[@class='fl pr']/em/text()").all())
    page.putField("EDUCATION",page.getHtml.xpath("//ul[@class='member_info_list fn-clear']/li[1]/div[2]/em/text()"))
    page.putField("SALARY",page.getHtml.xpath("//ul[@class='member_info_list fn-clear']/li[4]/div[2]/em/text()"))
    page.putField("NATION",page.getHtml.xpath("//ul[@class='member_info_list fn-clear']/li[8]/div[2]/em/text()"))
  }

  override def getSite: Site = {
    site
  }

}

class SjjySeleniumPipeline  extends Pipeline {

  Class.forName("oracle.jdbc.driver.OracleDriver")
  val url="jdbc:oracle:thin:@192.168.0.194:1521:smsdb"
  val user="smsdb"
  val password="chuanglan789"
  val properties=new Properties()
  properties.put("user",user)
  properties.put("password",password)
  val ss=SparkSession.builder().appName("partition").master("local[3]")
    .getOrCreate()
  import ss.implicits._

/*  def toOracle(s1:String,s2:String,s3:String,s4:String)={
    val conn=DriverManager.getConnection(url,user,password)
    val ps=conn.prepareStatement("INSERT INTO JIAYUAN (ID,EDUCATION,SALARY,NATION) VALUES(?,?,?,?)")
    ps.setString(1,s1)
    ps.setString(2,s2)
    ps.setString(3,s3)
    ps.setString(4,s4)
    ps.executeUpdate()
  }*/

  override def process(resultItems: ResultItems, task: Task): Unit = {
    val list=List((resultItems.get("ID").toString,resultItems.get("EDUCATION").toString,resultItems.get("SALARY").toString,resultItems.get("NATION").toString))
    val df=ss.sparkContext.parallelize(list).toDF("ID","EDUCATION","SALARY","NATION")
    df.write.mode("append").jdbc(url,"JIAYUAN",properties)
  }
}



object SjjySelenium{
  def main(args: Array[String]): Unit = {

    val add=Array(47692864,171118968)
    for(i<-0 to add.length-1){
      val num=add(i)
      Spider.create(new SjjySelenium)
        .addUrl(s"http://www.jiayuan.com/${num}")
        //.addPipeline(new ConsolePipeline())
        //.addPipeline(new JsonFilePipeline("D:\\jiayuan"))
        .addPipeline(new SjjySeleniumPipeline)
        .thread(5)
        .run()
    }


    //println(new SjjySelenium().site.getCookies)
    //new SjjySelenium().allCookies.foreach(println(_))

  }
}
