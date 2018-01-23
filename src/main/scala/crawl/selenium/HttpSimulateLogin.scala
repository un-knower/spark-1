package crawl.selenium

import java.io.{BufferedReader, InputStreamReader}

import org.apache.commons.httpclient.cookie.CookiePolicy
import org.apache.commons.httpclient.methods.{GetMethod, PostMethod}
import org.apache.commons.httpclient.{HttpClient, NameValuePair}
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.{DefaultHttpClient, HttpClients}
import org.apache.http.message.BasicNameValuePair
import org.apache.http.util.EntityUtils

import scala.collection.JavaConversions._
import scala.collection.mutable.ArrayBuffer
import scala.io.Source

/**
  * 模拟html表单提交
  */
class HttpSimulateLogin {

  def loginedPage() = {

    // val uriRequest=new HttpPost("http://login.jiayuan.com/")
    // uriRequest.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
    // uriRequest.setHeader("Accept-Encoding", "gzip, deflate")
    // uriRequest.setHeader("Accept-Language", "zh-CN,zh;q=0.9")
    // uriRequest.setHeader("Connection", "keep-alive")
    // uriRequest.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.84 Safari/537.36")

    /*
    //构建一个Client
    val client=new DefaultHttpClient()

    //构建一个POST请求
    val post=new HttpPost("http://login.jiayuan.com/")

    //构建表单参数
    val formParams=List[BasicNameValuePair]().toBuffer
    formParams.+=(new BasicNameValuePair("name","17321171953"))
    formParams.+=(new BasicNameValuePair("password","wzq123321"))
    //将表单参数转化为“实体”
    val entity=new UrlEncodedFormEntity(formParams.toList,"UTF-8")
    //将“实体“设置到POST请求里
    post.setEntity(entity)

    //提交POST请求，返回一个HttpResponse
    val response=client.execute(post)

/*    val cookie=response.getAllHeaders
   cookie.foreach(println(_))*/

    //Scala: InputStream to Array[Byte]
    val result=response.getEntity
    val content=result.getContent
    val lines=Source.fromInputStream(content).getLines()
    while(lines.hasNext){
      println(lines.next())
    }
  }
*/


    val loginUrl = "http://login.jiayuan.com/"
    val oriUrl="http://www.jiayuan.com/usercp/?from=login"
    val client = new HttpClient()
    // 模拟登陆，按实际服务器端要求选用 Post 或 Get 请求方式
    val postMethod = new PostMethod(loginUrl)
    // 设置登陆时要求的信息，用户名和密码
    val datas = Array[NameValuePair](new NameValuePair("name", "17321171953"), new NameValuePair("password", "wzq123321"))

    // 设置 HttpClient 接收 Cookie,用与浏览器一样的策略
    postMethod.setRequestBody(datas)
    client.getParams.setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY)
    client.executeMethod(postMethod)

    // 获得登陆后的 Cookie
    val cookies = client.getState.getCookies
    val tmpCookies=new StringBuilder
    for(i<- 0 to cookies.length-1){
      tmpCookies.append(cookies(i).toString+";")
    }

    val getMethod=new GetMethod(oriUrl)
    getMethod.setRequestHeader("cookie",tmpCookies.toString())

    val text=getMethod.getResponseBodyAsString
    println(text)


    //val getMethod=new GetMethod(oriUrl)



  }
}
  object HttpSimulateLogin {
    def main(args: Array[String]): Unit = {
      new HttpSimulateLogin().loginedPage()
    }
  }

