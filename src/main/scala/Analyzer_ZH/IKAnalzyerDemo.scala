package Analyzer_ZH

import java.io.{IOException, StringReader}

import org.apache.lucene.analysis.tokenattributes.{CharTermAttribute, OffsetAttribute, TypeAttribute}
import org.apache.lucene.analysis.{Analyzer, TokenStream}
import org.wltea.analyzer.lucene.IKAnalyzer


object IKAnalzyerDemo {
  def main(args: Array[String]): Unit = {
    // 构建IK分词器，使用smart分词模式
    //true:智能分词模式  false:最细粒度分词
    val analyzer:Analyzer=new IKAnalyzer(true)
    // 获取Lucene的TokenStream对象
    var ts:TokenStream=null
    try{
      ts=analyzer.tokenStream("",new StringReader(
        "东风里，朱门映柳，低按小秦筝。"))
      // 获取词元位置属性(元素所在的位置索引  包前不包后)
      val offset:OffsetAttribute=ts.addAttribute(classOf[OffsetAttribute])
      // 获取词元位置属性（元素term）
      val term:CharTermAttribute=ts.addAttribute(classOf[CharTermAttribute])
      // 获取词元位置属性（元素的属性 CN_WORD、ENGLISH）
      val typea:TypeAttribute=ts.addAttribute(classOf[TypeAttribute])
      // 重置TokenStream（重置StringReader）
      ts.reset()
      // 迭代获取分词结果

      /**
0 - 2 : 这是 | CN_WORD
2 - 4 : 一个 | CN_WORD
4 - 6 : 中文 | CN_WORD
21 - 30 : ikanalyer | ENGLISH
31 - 34 : can | ENGLISH

0 - 3 : 武汉市 | CN_WORD
3 - 7 : 长江大桥 | CN_WORD
        */
      while (ts.incrementToken()){
        println(offset.startOffset() + " - " + offset.endOffset() + " : "
          + term.toString() + " | " + typea.`type`())
      }
      ts.end()
    }catch {
      case e:IOException=>e.printStackTrace()
    }finally {
      if(ts!=null){
        try{
          ts.close()
        }catch {
          case e:IOException=>e.printStackTrace()
        }
      }
    }
  }
}
