package selenium

import java.util.concurrent.TimeUnit

import org.openqa.selenium.chrome.{ChromeDriver, ChromeDriverService}

object ChromeLaunch {
  def main(args: Array[String]): Unit = {

    //加载chrom驱动
    System.setProperty("webdriver.chrome.driver", "D:\\chromedriver_win32\\chromedriver.exe")
    val driver=new ChromeDriver()
    //将窗口最大化
    driver.manage().window().maximize()
    //设置隐性等待时间,，因为点击后网站一段时间后才能返回内容，如果不等待会报超时异常。
    driver.manage.timeouts.implicitlyWait(8, TimeUnit.SECONDS)
    //使用get打开一个站点
    driver.get("https://www.baidu.com")
    //关闭并退出浏览器
    //driver.quit()

    //浏览器后退
    //driver.navigate().back()
  }
}
