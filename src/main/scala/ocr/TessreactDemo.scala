package ocr

import java.io.File
import javax.imageio.ImageIO

import net.sourceforge.tess4j.Tesseract
import net.sourceforge.tess4j.util.ImageHelper

object TessreactDemo {
  def main(args: Array[String]): Unit = {

    //https://github.com/tesseract-ocr/tesseract

    /**
      * 经验证，只对类似截图的英文、数字很好的识别
      */

    val image=new File("images/fetch.png")

    val tessreact=new Tesseract
    //在工程中，必须要有tessdata目录，存放语言库，如eng.traineddata，chi_sim.traineddata等

    // tessreact.setDatapath("./tessdata")

    //处理英文，在tessdata中必须要有eng.traineddata文件
    tessreact.setLanguage("eng")

    //处理中文，在tessdata中必须要有chi_sim.traineddata文件
    //tessreact.setLanguage("chi_sim")

    //不能识别条形码
    val result=tessreact.doOCR(image)
    //println(result)



    //图片处理提高识别率
    //val gray=ImageHelper.convertImageToGrayscale(ImageHelper.getSubImage())
    //ImageIO.write(gray,"png",new File("images/4.png"))


  }
}
