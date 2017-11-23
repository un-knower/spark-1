package Stanfordcorenlp.SimpleCoreNLP



import edu.stanford.nlp.ling.CoreAnnotations
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation
import edu.stanford.nlp.pipeline.{Annotation, StanfordCoreNLP}

import scala.collection.JavaConversions._

class LemmatizationZH {

  def stemmed(text:String): String ={
    val pipeline=new StanfordCoreNLP("StanfordCoreNLP-chinese.properties")
    val document=new Annotation(text)
    pipeline.annotate(document)
    val sentences=document.get(classOf[CoreAnnotations.SentencesAnnotation])
    var out=""
    for(sentence<-sentences){
      for(token<-sentence.get(classOf[CoreAnnotations.TokensAnnotation])){
        val lemma=token.get(classOf[LemmaAnnotation])
        //注意在分句后需要加入分隔符，否则默认没有分隔符，看起来结果没有差别
        out=out+lemma+"|"
      }
    }
    out
  }
}

object LemmatizationZH{
  def main(args: Array[String]): Unit = {
    val lemma=new LemmatizationZH
    val text="lxw的大数据田地 -- lxw1234.com 专注Hadoop、Spark、Hive等大数据技术博客。 北京优衣库"
    println("原句:"+text)
    println("词干化:"+lemma.stemmed(text))
  }
}
