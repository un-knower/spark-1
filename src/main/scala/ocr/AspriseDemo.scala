package ocr

import java.io.File

import com.asprise.ocr.Ocr


object AspriseDemo {
  def main(args: Array[String]): Unit = {

    //http://asprise.com/ocr/docs/html/asprise-ocr-api-java.html

    //启动ocr的ui界面
    //Ocr.main(args)

    Ocr.setUp()

    //创建ocr引擎
    val ocr=new Ocr
    ocr.startEngine("eng",Ocr.SPEED_FAST)

    //识别字符和条形码，并指定输出格式
    //val result=ocr.recognize(Array(new File("images/2.png"),new File("images/3.png")),Ocr.RECOGNIZE_TYPE_ALL,Ocr.OUTPUT_FORMAT_XML)

    //当为pdf或者rtf格式时，需要指定输出的属性
    val result=ocr.recognize(Array(new File("images/2.png"),new File("images/3.png")),Ocr.RECOGNIZE_TYPE_ALL,Ocr.OUTPUT_FORMAT_PDF,

      /**
        * 第一种方式：使用'|'分隔属性 "PROP_PDF_OUTPUT_FILE=ocr-result.pdf|PROP_PDF_OUTPUT_TEXT_VISIBLE=true"
        * 第二种方式：使用PropertyBuilder  new Ocr.PropertyBuilder().setPdfOutputFile("ocr-result.pdf").setPdfTextVisible(true)
        */
      "PROP_PDF_OUTPUT_FILE=ocr-result.pdf|PROP_PDF_OUTPUT_TEXT_VISIBLE=true"
    )

    val result2=ocr.recognize(Array(new File("images/2.png")),Ocr.RECOGNIZE_TYPE_ALL,Ocr.OUTPUT_FORMAT_RTF,
      "PROP_RTF_OUTPUT_FILE=ocr-result.rtf"
    )

    //指定文本
    val resultText=ocr.recognize(Array(new File("images/2.png")),Ocr.RECOGNIZE_TYPE_TEXT,Ocr.OUTPUT_FORMAT_PLAINTEXT)

    //指定条形码
    val resultBar=ocr.recognize(Array(new File("images/2.png")),Ocr.RECOGNIZE_TYPE_BARCODE,Ocr.OUTPUT_FORMAT_PLAINTEXT)





    ocr.stopEngine()
  }
}
