package selenium

import java.util.concurrent.TimeUnit

import org.openqa.selenium.firefox.FirefoxDriver

object FirefoxLaunch {
  def main(args: Array[String]): Unit = {

    //当找不到浏览器时报错Cannot find firefox binary in PATH,需要设置浏览器地址
    System.setProperty("webdriver.firefox.bin", "D:\\Program Files (x86)\\firefox.exe")
    System.setProperty("webdriver.firefox.marionette", "D:\\geckodriver-v0.19.1-win64\\geckodriver.exe")
    val driver=new FirefoxDriver()
    driver.manage().window().maximize()
    driver.manage.timeouts.implicitlyWait(8, TimeUnit.SECONDS)
    driver.get("https://www.baidu.com")

  }
}
