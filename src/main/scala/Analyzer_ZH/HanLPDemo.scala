package Analyzer_ZH

import com.hankcs.hanlp.HanLP
import com.hankcs.hanlp.tokenizer.{IndexTokenizer, NLPTokenizer}

object HanLPDemo {
  def main(args: Array[String]): Unit = {
    val text="东风里，朱门映柳，低按小秦筝。"
    //标准分词，最常用。对StandardTokenizer.segment的包装。
    val termList1 = HanLP.segment(text)
    for (i<- 0 to termList1.size()-1){
      println(termList1.get(i))
    }

    //NLP分词,执行全部命名实体识别和词性标注
    println(NLPTokenizer.segment(text))

    //索引分词，面向搜索引擎的分词器，能够对长词全切分
    val termList2=IndexTokenizer.segment(text)
    for (i<- 0 to termList2.size()-1){
      val term=termList2.get(i)
      println(term + " [" + term.offset + ":" + (term.offset + term.word.length()) + "]")
    }
  }
}
