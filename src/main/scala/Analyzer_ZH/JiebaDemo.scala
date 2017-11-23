package Analyzer_ZH

import com.huaban.analysis.jieba.JiebaSegmenter
import com.huaban.analysis.jieba.JiebaSegmenter.SegMode

object JiebaDemo {
  def main(args: Array[String]): Unit = {
    val sentences=Array("这是一个伸手不见五指的黑夜。我叫孙悟空，我爱北京，我爱Python和C++。",
    "我不喜欢日本和服。","工信处女干事每月经过下属科室都要亲口交代24口交换机等技术性器件的安装工作",
    "结果婚的和尚未结过婚的")
    val segment=new JiebaSegmenter
    sentences.foreach(i=>{
      //println(segment.process(i,SegMode.INDEX).toString)
      println(segment.process(i,SegMode.SEARCH).toString)
    })
  }
}
