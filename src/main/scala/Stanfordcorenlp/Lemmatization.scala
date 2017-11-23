package Stanfordcorenlp

import java.util.Properties

import edu.stanford.nlp.ling.CoreAnnotations
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation
import edu.stanford.nlp.pipeline.{Annotation, StanfordCoreNLP}

import scala.collection.JavaConversions._

class Lemmatization {

  def stemmed(text:String): String ={
    val props=new Properties()
    props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref")
    val pipeline=new StanfordCoreNLP(props)
    val document=new Annotation(text)
    pipeline.annotate(document)
    val sentences=document.get(classOf[CoreAnnotations.SentencesAnnotation])
    var out=""
    for(sentence<-sentences){
      for(token<-sentence.get(classOf[CoreAnnotations.TokensAnnotation])){
        val lemma=token.get(classOf[LemmaAnnotation])
        out=out+lemma+" "
      }
    }
    out
  }
}

object Lemmatization{
  def main(args: Array[String]): Unit = {
    val lemma=new Lemmatization
    val text=" "
    println("原句:"+text)
    println("词干化:"+lemma.stemmed(text))
  }
}
