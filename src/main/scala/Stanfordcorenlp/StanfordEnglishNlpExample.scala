package Stanfordcorenlp

import java.util.Properties

import edu.stanford.nlp.coref.CorefCoreAnnotations.CorefChainAnnotation
import edu.stanford.nlp.dcoref.CorefCoreAnnotations
import edu.stanford.nlp.ling.CoreAnnotations
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation
import edu.stanford.nlp.pipeline.{Annotation, StanfordCoreNLP}
import edu.stanford.nlp.semgraph.{SemanticGraph, SemanticGraphCoreAnnotations}
import edu.stanford.nlp.trees.TreeCoreAnnotations
import edu.stanford.nlp.util.PropertiesUtils

import scala.collection.JavaConversions._

class StanfordEnglishNlpExample {

  /**
    * 1.构造一个StanfordCoreNLP对象，配置NLP的功能
    * 2.创造一个空的Annotation对象，包含各个组件
    * tokenize（分词）, ssplit（断句）, pos（词性标注）, lemma（词干化）, ner（命名实体识别）, parse（语法分析）, dcoref（同义词分辨）
    * 3.props可以设置多个属性值
    */


  def runAllAnnotators(text:String):Unit={
    val props=new Properties()
    props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref")

    //Caseless models更佳或者使用 truecase
    props.setProperty("pos.model", "edu/stanford/nlp/models/pos-tagger/english-caseless-left3words-distsim.tagger")
    props.setProperty("ner.model", "edu/stanford/nlp/models/ner/english.all.3class.caseless.distsim.crf.ser.gz," +
      "edu/stanford/nlp/models/ner/english.muc.7class.caseless.distsim.crf.ser.gz," +
      "edu/stanford/nlp/models/ner/english.conll.4class.caseless.distsim.crf.ser.gz")
    val pipeline=new StanfordCoreNLP(props)

    //PropertiesUtils设置多个属性值
/*    val pipeline=new StanfordCoreNLP(PropertiesUtils.asProperties(
      "annotators", "tokenize,ssplit,pos,lemma,parse,natlog",
      "ssplit.isOneSentence", "true",
      "parse.model", "edu/stanford/nlp/models/srparser/englishSR.ser.gz",
      "tokenize.language", "en"
    ))*/

    //对text执行所有的Annotators
    val document=new Annotation(text)
    pipeline.annotate(document)

  /**
    * 获取文本处理结果，sentences包含所有分析结果
    */

    val sentences=document.get(classOf[CoreAnnotations.SentencesAnnotation])

    for(sentence<-sentences){
      for(token<-sentence.get(classOf[CoreAnnotations.TokensAnnotation])){

        //获取句子的token（可以是分词后的短语）
        val word=token.get(classOf[CoreAnnotations.TextAnnotation])
        //词性标注
        val pos=token.get(classOf[CoreAnnotations.PartOfSpeechAnnotation])
        // 命名实体识别
        val ne=token.get(classOf[CoreAnnotations.NamedEntityTagAnnotation])
        //词干化处理
        val lemma=token.get(classOf[LemmaAnnotation])

        println(word+"\t"+pos+"\t"+lemma+"\t"+ne)
      }
      val tree=sentence.get(classOf[TreeCoreAnnotations.TreeAnnotation])
      println("句子的解析树 :")
     // println(tree.toString)
      tree.pennPrint()
      val graph =sentence.get(classOf[SemanticGraphCoreAnnotations.EnhancedPlusPlusDependenciesAnnotation])
      println("句子的依赖图 :")
      //println(graph .toString())
      println(graph.toString(SemanticGraph.OutputFormat.LIST))
    }

    /**
      * 指代词链
      * 每条链保存指代的集合
      * 句子和偏移量都从1开始
      */
    val corefChains =document.get(classOf[CorefCoreAnnotations.CorefChainAnnotation])
/*
    for(i<-corefChains.entrySet()){
        println("Chain " + i.getKey() + " ")
        for(j<-i.getValue.getMentionsInTextualOrder){
          val tokens=sentences.get(j.sentNum-1).get(classOf[CoreAnnotations.TokensAnnotation])
          println(" "+j+", i.e., 0-based character offsets ["+tokens.get((j.startIndex-1)).beginPosition()+"," +
            tokens.get(j.endIndex-2).endPosition()+")")
        }
      }*/
    }
}

object StanfordEnglishNlpExample{
  def main(args: Array[String]): Unit = {
    val example=new StanfordEnglishNlpExample()
    example.runAllAnnotators("judy has been to china . She likes people there . And she went to beijing")

    /**
      * 结果解释：
1.
judy  --原词
NN    --词性为名词
O     --命名实体对象识别结果为O,
judy  --词干识别为judy


      */
  }
}

