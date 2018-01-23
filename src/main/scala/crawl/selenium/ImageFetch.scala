package crawl.selenium

import java.io._
import java.util.concurrent.TimeUnit
import javax.imageio.ImageIO

import org.apache.commons.io.FileUtils

import org.openqa.selenium.{By, OutputType}
import org.openqa.selenium.chrome.ChromeDriver


object ImageFetch{
  def main(args: Array[String]): Unit = {

    val driver=new ChromeDriver()
    driver.manage().window().maximize()
    driver.manage.timeouts.implicitlyWait(10, TimeUnit.SECONDS)
    driver.get("http://qianjiye.de/2015/08/tesseract-ocr")

    /**
      * 1、获取到元素的大小、元素的坐标
      * 2、截取整屏
      * 3、根据元素的坐标和大小，定位要剪裁的区域
      * 4、使用图像库对元素区域进行剪裁
      */
    //获取验证码位置
    val ele=driver.findElement(By.xpath("/html/body/div[2]/div/article/section[2]/p[5]/img"))

    //获取整个页面截屏
    val screenShot=driver.getScreenshotAs(OutputType.FILE)
    val fullImage=ImageIO.read(screenShot)

    //获取验证码的坐标，用坐标的方法保存验证码图片
    val point=ele.getLocation
    val width=ele.getSize.getWidth
    val height=ele.getSize.getHeight

    //获得验证码的截图
    val eleScreenShot=fullImage.getSubimage(point.getX,point.getY,width,height)
    ImageIO.write(eleScreenShot,"png",screenShot)

    //保存验证码截图到文件
    val screenshotLocation = new File("images/fetch.png")
    FileUtils.copyFile(screenShot,screenshotLocation)



  }
}