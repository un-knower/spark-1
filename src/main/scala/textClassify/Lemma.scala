package textClassify

import java.util.Properties

import edu.stanford.nlp.ling.CoreAnnotations._
import edu.stanford.nlp.pipeline._

import scala.collection.mutable.ArrayBuffer

import scala.collection.JavaConversions._


class Lemma {

    /**
      * 词干提取Lemmatization：把一个任何形式的语言词汇还原为一般形式（能表达完整语义）,词形还原，基于字典
      * e.g:driving—>drive  drove—>drive
      * @param text
      * @param stopWords
      * @return
      */

    //结果：转换为小写，去除停用词，去除空格，转换为Array[String]
/*    def plainTextToLemmas(text: String,stopWords:Array[String]): Array[String] = {
      val props = new Properties()
      props.put("annotators", "tokenize, ssplit, pos, lemma")
      val pipeline = new StanfordCoreNLP(props)
      val doc = new Annotation(text)
      pipeline.annotate(doc)
      val lemmas = new ArrayBuffer[String]()
      val sentences = doc.get(classOf[SentencesAnnotation])
      for (sentence <- sentences) {
        //此处为java List ，需要转换
        for (token <- sentence.get(classOf[TokensAnnotation])) {
          val lemma = token.get(classOf[LemmaAnnotation])
          if (lemma.length > 2 && !stopWords.contains(lemma)) {
            lemmas += lemma.toLowerCase
          }
        }
      }
      lemmas.toArray
    }*/

    def plainTextToLemmas(text: String,stopWords:Array[String]): String = {
      val props = new Properties()
      props.put("annotators", "tokenize, ssplit, pos, lemma")
      val pipeline = new StanfordCoreNLP(props)
      val doc = new Annotation(text)
      pipeline.annotate(doc)
      var lemmas =""
      val sentences = doc.get(classOf[SentencesAnnotation])
      for (sentence <- sentences) {
        //此处为java List ，需要转换
        for (token <- sentence.get(classOf[TokensAnnotation])) {
          val lemma = token.get(classOf[LemmaAnnotation])
          if (lemma.length > 2 && !stopWords.contains(lemma)) {
            lemmas =lemmas+lemma+","
          }
        }
      }
      lemmas
    }


}

