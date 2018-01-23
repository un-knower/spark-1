package selenium

import java.io.{FileOutputStream, ObjectOutputStream}
import java.util.concurrent.TimeUnit

import org.apache.http.impl.client.BasicCookieStore
import org.apache.http.impl.cookie.BasicClientCookie
import org.openqa.selenium.{By, WebElement}
import org.openqa.selenium.chrome.ChromeDriver

import scala.collection.JavaConversions._

/**
  * 模拟csdn登录，获取登录后的cookies保存到文件
  */
object CsdnLaunch {
  def main(args: Array[String]): Unit = {

    System.setProperty("webdriver.chrome.driver", "D:\\chromedriver_win32\\chromedriver.exe")
    val driver=new ChromeDriver()
    driver.manage().window().maximize()
    driver.manage.timeouts.implicitlyWait(10, TimeUnit.SECONDS)
    driver.get("http://login.jiayuan.com/")

    //获取页面元素
/*    val userName=driver.findElement(By.name("username"))
    val password=driver.findElement(By.name("password"))
    val autoLogin=driver.findElement(By.name("rememberMe"))
    val login=driver.findElement(By.className("logging"))*/

    val userName=driver.findElement(By.id("login_email"))
    val password=driver.findElement(By.id("login_password"))
    val login=driver.findElement(By.id("login_btn"))

    //操作页面元素
    userName.sendKeys("17321171953")
    password.sendKeys("wzq123321")
    //autoLogin.click()

    //提交表单
    login.submit()
    Thread.sleep(5000)

    //获取cookies
    val cookies=driver.manage().getCookies.toList
    //println(cookies.size())

    //保存cookies到org.apache.http.client.CookieStore中，然后序列化到文件中，用于下次读取，用httpclient来抓数据。
    val cookiesStore=new BasicCookieStore

    for(i<- 0 to cookies.size()-1){
      val basicClientCookie=new BasicClientCookie(cookies(i).getName,cookies(i).getValue)
      //basicClientCookie.setDomain(cookies(i).getDomain)
      //basicClientCookie.setPath(cookies(i).getPath)
      cookiesStore.addCookie(basicClientCookie)
    }

    //val oos=new ObjectOutputStream(new FileOutputStream("C:\\Users\\ChuangLan\\Desktop\\cookies.txt"))
   // oos.writeObject(cookiesStore)

    cookiesStore.getCookies.foreach(println(_))
  }
}
