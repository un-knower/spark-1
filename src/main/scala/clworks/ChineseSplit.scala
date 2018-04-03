package clworks

import java.io.StringReader

import com.hankcs.hanlp.HanLP
import com.huaban.analysis.jieba.JiebaSegmenter
import com.huaban.analysis.jieba.JiebaSegmenter.SegMode
import edu.stanford.nlp.ling.CoreAnnotations
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation
import edu.stanford.nlp.pipeline.{Annotation, StanfordCoreNLP}
import org.ansj.splitWord.analysis.ToAnalysis
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute
import org.apdplat.word.WordSegmenter
import org.wltea.analyzer.lucene.IKAnalyzer

import scala.collection.JavaConversions._
import scala.collection.mutable.ArrayBuffer

class ChineseSplit {

  //StanfordCoreNLP分词
  def runChineseAnnotators(text: String,stopWords:Array[String]):Array[String]={
    val pipeline=new StanfordCoreNLP("StanfordCoreNLP-chinese.properties")
    val document=new Annotation(text)
    pipeline.annotate(document)
    var words = new ArrayBuffer[String]()
    val sentences=document.get(classOf[CoreAnnotations.SentencesAnnotation])
    for(sentence<-sentences){
      for(token<-sentence.get(classOf[CoreAnnotations.TokensAnnotation])){
        val word = token.get(classOf[TextAnnotation])
        //注意在分句后需要加入分隔符，否则默认没有分隔符，看起来结果没有差别
        //words=words+word+" "
        //加上停用词并判断每个字符串的长度，否则会报数组越界
        if (!stopWords.contains(word)) {
          words+=word+"->"
        }
      }
    }
    words.toArray
  }
/*def runChineseAnnotators(text: String,stopWords:Array[String]):String={
  val pipeline=new StanfordCoreNLP("StanfordCoreNLP-chinese.properties")
  val document=new Annotation(text)
  pipeline.annotate(document)
  var words = ""
  val sentences=document.get(classOf[CoreAnnotations.SentencesAnnotation])
  for(sentence<-sentences){
    for(token<-sentence.get(classOf[CoreAnnotations.TokensAnnotation])){
      val word = token.get(classOf[TextAnnotation])
      //注意在分句后需要加入分隔符，否则默认没有分隔符，看起来结果没有差别
      //words=words+word+" "
      //加上停用词并判断每个字符串的长度，否则会报数组越界
      if (!stopWords.contains(word)) {
        words+=word+"->"
      }
    }
  }
  words
}*/

  //Ansj分词
/*  def runAnsjSplit(text:String):Array[String]={
    val parse=ToAnalysis.parse(text)
    val terms=parse.getTerms
    val arr=new ArrayBuffer[String]()
    for(i<-0 to terms.size()-1){
      val word=terms.get(i).getNatureStr
      arr+=word
    }
    arr.toArray
  }*/
def runAnsjSplit(text:String):String={
  val parse=ToAnalysis.parse(text)
  val terms=parse.getTerms
  val arr=new StringBuilder
  for(i<-0 to terms.size()-1){
    val word=terms.get(i).getNatureStr
    arr.append(word)+"->"
  }
  arr.toString()
}

  def runHanLPSplit(text:String):Array[String]={
    val terms=HanLP.segment(text)
    val arr=new ArrayBuffer[String]()
    for(i<- 0 to terms.size()-1){
      val word=terms.get(i).word
      arr+=word+"->"
    }
    arr.toArray
  }

  def runIKAnalyzerSplit(text:String):Array[String]={
    val analyser=new IKAnalyzer(true)
    val ts=analyser.tokenStream("",new StringReader(text))
    val terms=ts.addAttribute(classOf[CharTermAttribute])
    ts.reset()
    val arr=new ArrayBuffer[String]()
    while(ts.incrementToken()){
      val word=terms.toString
      arr+=word+"->"
    }
    arr.toArray
  }

  def runJiebaSplit(text:String):Array[String]={
    val segment=new JiebaSegmenter
    val terms=segment.process(text,SegMode.SEARCH)
    val arr=new ArrayBuffer[String]()
    for (i<-0 to terms.size()-1){
      val word=terms.get(i).word
      arr+=word
    }
    arr.toArray
  }

  def runWordSplit(text:String):Array[String]={
    val wordsWithStop=WordSegmenter.segWithStopWords(text)
    val arr=new ArrayBuffer[String]()
    for(i<-0 to wordsWithStop.size()-1){
      val word=wordsWithStop.get(i).getText
      arr+=word+"->"
    }
    arr.toArray
  }

}

