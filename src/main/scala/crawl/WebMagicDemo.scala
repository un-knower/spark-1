package crawl

import java.util.Properties

import org.apache.spark.sql.{Row, SparkSession}
import org.apache.spark.sql.types.{StringType, StructField, StructType}
import us.codecraft.webmagic._
import us.codecraft.webmagic.pipeline.{ConsolePipeline, FilePipeline, JsonFilePipeline, Pipeline}
import us.codecraft.webmagic.processor.PageProcessor
import us.codecraft.webmagic.processor.example.GithubRepoPageProcessor

import scala.collection.JavaConversions._

/**
  * WebMagic的四个组件
  * 1.Downloader:从互联网上下载页面，以便后续处理。使用Apache HttpClient下载
  * 2.PageProcessor：解析页面，抽取有用信息，以及发现新的链接。使用Jsoup作为HTML解析
  * 3.Scheduler：管理待抓取的URL，以及一些去重的工作
  * 4.Pipeline：抽取结果的处理，包括计算、持久化到文件、数据库等。输出到控制台或保存到文件
  */

/**
  * 自定义PageProcessor
  */
class CompanyProcessor extends PageProcessor{


  //对站点本身的一些配置信息，例如编码、HTTP头、超时时间、重试策略等、代理等，可以通过设置Site对象来进行配置。
  val urlList="http://blog\\.sina\\.com\\.cn/s/articlelist_1487828712_0_\\d+\\.html"  //列表
  val urlPost="http://blog\\.sina\\.com\\.cn/s/blog_\\w+\\.html"   //列表对应连接

  //对于某些省略url地址的时候，必须加上域名地址限定
  val site=Site.me().setDomain("blog.sina.com.cn").setSleepTime(3000)
    .setUserAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36")

  //val site=Site.me().setRetryTimes(3).setSleepTime(3000)

  override def getSite: Site = {
    this.site
  }

  // process是定制爬虫逻辑的核心接口，在这里编写抽取逻辑
  override def process(page: Page): Unit = {
    /**
      * 页面元素的抽取，主要使用了三种抽取技术：XPath、正则表达式和CSS选择器
      * XPath查询语言：//h1[@class='entry-title public']/strong/a/text() 查找所有class属性为'entry-title public'的h1元素，并找到他的strong子节点的a子节点，并提取a节点的文本信息
      * 加上text()可以获得标签内容
      * css选择器
      * 正则表达式  regex
      * links:选择所有链接
      */
/*    page.putField("书名", page.getHtml().xpath("//h4[@class='title']/a/text()").toString())
    page.putField("作者", page.getHtml().xpath("//div[@class='author']/span/text()").toString())*/

    //区分是列表页面还是信息页面

    if(page.getUrl.regex(urlList).`match`()){
      page.addTargetRequests(page.getHtml.xpath("//div[@class='articleList']").links().regex(urlPost).all())
      page.addTargetRequests(page.getHtml().links().regex(urlList).all())
    }else{
      //取得博客文章标题
      page.putField("title", page.getHtml().xpath("//div[@class='articalTitle']/h2/text()").get())
      //若直接复制当前内容xpath会有许多null值，不同页面的id不同，一定要找出不同页面的相同地方
      //page.putField("title", page.getHtml().xpath("//*[@id=\"t_58ae76e80100to5q\"]/text()").get())
      //取得文章内容
     // page.putField("content", page.getHtml().xpath("//div[@id='articlebody']//div[@class='articalContent']/text()").get())
      //page.putField("content", page.getHtml().xpath("//*[@id=\"sina_keyword_ad_area2\"]/text()").get())
      //取得创建时间，取得括号内的时间
      page.putField("date", page.getHtml().xpath("//div[@id='articlebody']//span[@class='time SG_txtc']/text()").regex("\\((.*)\\)").get())

    }

     }
}

/**
  * 自定义pipeline，主要用于抽取结果的保存
  * 一个Spider可以有多个Pipeline
  * 实现Pipeline接口
  */
class OraclePipiline extends Pipeline{

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


  /**
    *
    * @param resultItems    ResultItems保存了抽取结果，它是一个Map结构。在page.putField(key,value)中保存的数据，可以通过ResultItems.get(key)获取
    * @param task
    */
  override def process(resultItems: ResultItems, task: Task): Unit = {
   /*if(resultItems.get("title") != null & resultItems.get("date") != null){
     val df=ss.sparkContext.parallelize(List((resultItems.get("title").toString,resultItems.get("date").toString))).toDF()
     df.write.mode("append").jdbc(url,"CRAWL",properties)
   }*/
    val list=List((Option(resultItems.get("title")).getOrElse("0"),Option(resultItems.get("date")).getOrElse("1")))
    val df=ss.sparkContext.parallelize(list).toDF()
    df.write.mode("append").jdbc(url,"CRAWL",properties)
  }
}


object WebMagicDemo {
  def main(args: Array[String]): Unit = {
    /**
      * Spider爬虫启动的入口,是WebMagic内部流程的核心
      * 也是WebMagic操作的入口，它封装了爬虫的创建、启动、停止、多线程等功能
      */
    Spider.create(new CompanyProcessor)
      //添加初始的url
      .addUrl("http://blog.sina.com.cn/s/articlelist_1487828712_0_1.html")
      //设置Scheduler，使用Redis来管理URL队列
      //.setScheduler(new RedisScheduler("localhost"))
      //设置Pipeline，将结果以json方式保存到文件
      //.addPipeline(new JsonFilePipeline("D:\\webmagic"))
     // .addPipeline(new FilePipeline("D:\\webmagic"))
      .addPipeline(new OraclePipiline)
      //开启5个线程同时执行
      .thread(10)
      //启动爬虫
      .run()

    /**
      * ConsolePipeline输出结果
get page: https://read.douban.com/?dcs=top-nav&dcm=douban
书名:	我们都一样孤独无依
作者:	坦克手贝吉塔
      */
  }
}
