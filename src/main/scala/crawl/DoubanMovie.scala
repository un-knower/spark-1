package crawl

import java.io.{File, PrintWriter}
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

import scala.collection.parallel.ForkJoinTaskSupport
import scala.concurrent.forkjoin.ForkJoinPool
import scala.util.{Failure, Success, Try}
import scala.collection.JavaConversions._
import scala.collection.mutable.ArrayBuffer

object DoubanMovie {
  //要访问的连接
  val url="https://movie.douban.com/tag/%s?start=%d&type=T"
  //需要抓取的标签和页数
  val tags=Map(
    "经典" -> 4, //tag，页数
    "爱情" -> 4,
    "动作" -> 4,
    "剧情" -> 4,
    "悬疑" -> 4,
    "文艺" -> 4,
    "搞笑" -> 4,
    "战争" -> 4
  )

  /**
    * 解析Html
    * @param doc  解析Document，需要对照网页源码进行解析
    * @param movies
    * @return
    */
  def parseDoc(doc:Document,movies:ConcurrentHashMap[String,String])={
    var count=0
    //通过使用CSS（或Jquery）selector syntax 获得你想要操作元素，这里获得的是说有class=item的<tr/>标签。
    for(element<-doc.select("tr.item")){
      movies.put(
        //attr函数：通过属性获得Element中第一个匹配该属性的值
        element.select("a.nbg").attr("title"), element.select("a.nbg").attr("title")+"->"+  //获得标题
          element.select("a.nbg").attr("href")+"->"+   //豆瓣连接
          //html函数：获得element中包含的Html内容
          //element.select("p.pl").html()+"->"+   //电影简介
          element.select("span.rating_nums").html()+"->"+   //评分
          element.select("span.pl").html()   //评论数
      )
      count+=1
    }
    count
  }


  //用于记录总数，和失败次数
  val sum,fail=new AtomicInteger(0)

  /**
    * 建立连接获得对应Url的Html
    * 当返回异常时模式匹配会匹配Failure(e)，并将异常赋值给模板类中的e
    * 当返回成功时将匹配Success(doc)，并将获得的Html的Document赋值给doc。
    * @param times
    * @param delay   延时时间
    * @param url    抓取的Url
    * @param movies   存取抓到的内容
    */
  def requestGetUrl(times: Int = 100, delay: Long = 10000)(url: String, movies: ConcurrentHashMap[String, String]): Unit = {
    /**
      * 从一个网站获取和解析一个HTML文档,使用get方式
      */
    Try(Jsoup.connect(url).get()) match{
      //当出现异常时10s后重试,异常重复100次
      case Failure(e)=>
        if(times!=0){
          println(e.getMessage)
          fail.addAndGet(1)
          Thread.sleep(delay)
          requestGetUrl(times-1,delay)(url,movies)
        }else throw e
      case Success(doc)=>
        val count=parseDoc(doc,movies)
        if(count==0){
          Thread.sleep(delay)
          requestGetUrl(times-1,delay)(url,movies)
        }
        sum.addAndGet(count)
    }
  }

  /**
    *  使用并发集合,多线程抓取
    * @param url   原始的Url
    * @param tag    电影标签
    * @param maxPage    页数
    * @param threadNum   线程数
    * @param movies   并发集合存取抓到的内容
    */
  def concurrentCrawler(url: String, tag: String, maxPage: Int, threadNum: Int, movies: ConcurrentHashMap[String, String]) = {
    val loopPar=(0 to maxPage).par
    //设置并发数
    loopPar.tasksupport=new ForkJoinTaskSupport(new ForkJoinPool(threadNum))
    // 利用并发集合多线程同步抓取:遍历所有页
    loopPar.foreach(i => requestGetUrl()(url.format(URLEncoder.encode(tag, "UTF-8"), 20 * i), movies))
    saveFile1(tag,movies)
  }

  //直接输出
  def saveFile(file: String, movies: ConcurrentHashMap[String, String]) ={
    val writer=new PrintWriter(new File(new SimpleDateFormat("yyyyMMdd").format(new Date())+"_"+file+".txt"))
    for((_,v)<-movies){
      writer.println(v)
    }
    writer.close()
  }

  //排序输出到文件
  def saveFile1(file: String, movies: ConcurrentHashMap[String, String]) ={
    val writer=new PrintWriter(new File(new SimpleDateFormat("yyyyMMdd").format(new Date())+"_"+file+".txt"))
    val col=new ArrayBuffer[String]()
    for((_,value)<-movies){
      col+=value
    }
    val sort=col.sortWith(
      (i,j)=>{
        val s1=i.split("->")(2)
        val s2=j.split("->")(2)
        if (s1 == null || s2 == null || s1.isEmpty || s2.isEmpty) {
          true
        } else {
          s1.toFloat > s2.toFloat
        }
      }
    )
    sort.foreach(writer.println(_))
    writer.close()
  }

  def main(args: Array[String]): Unit = {
    val threadNum=30
    val t1=System.currentTimeMillis()
    for((tag,page)<-tags){
      concurrentCrawler(url, tag, page, threadNum, new ConcurrentHashMap[String, String]())
    }
    val t2=System.currentTimeMillis()
    println(s"抓取数:${sum}--重试数:${fail}--耗时数:${(t2-t1)/1000}")
  }
}
