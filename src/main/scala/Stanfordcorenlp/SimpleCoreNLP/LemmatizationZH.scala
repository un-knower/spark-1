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
        out=out+lemma+" "
      }
    }
    out
  }
}

object LemmatizationZH{
  def main(args: Array[String]): Unit = {
    val lemma=new LemmatizationZH
    val text="克林顿说，华盛顿将逐步落实对韩国的经济援助。金大中对克林顿的讲话报以掌声：克林顿总统在会谈中重申，他坚定地支持韩国摆脱经济危机"
    println("原句:"+text)
    println("词干化:"+lemma.stemmed(text))
  }
}
