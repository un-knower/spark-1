package Analyzer_ZH

import java.io.StringReader
import java.util

import org.lionsoul.jcseg.extractor.impl.TextRankKeywordsExtractor
import org.lionsoul.jcseg.tokenizer.core._

import scala.collection.mutable.ArrayBuffer

object JcsegDemo {
  def main(args: Array[String]): Unit = {
    val text="手机电子书    abc   http://www.sjshu.com"

    //从jcseg.properties配置文件中初始化配置
    val config=new JcsegTaskConfig(true)
    //创建默认词库，依据给定的JcsegTaskConfig配置实例自主完成词库的加载
    val dic=DictionaryFactory.createDefaultDictionary(config,true)

    val seg=SegmentFactory.createJcseg(JcsegTaskConfig.COMPLEX_MODE,config,dic)
    seg.reset(new StringReader(text))
    var word:IWord=null

/*    while ( (word = seg.next()) != null ){
      if(word!=null){
       // println(word.getValue)
      }
    }*/

    //关键字提取
    val extractor=new TextRankKeywordsExtractor(seg)
    val keywords=extractor.getKeywords(new StringReader(text))
    println(keywords)
  }
}
