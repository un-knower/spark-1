package Stanfordcorenlp

import edu.stanford.nlp.ling.CoreAnnotations
import edu.stanford.nlp.pipeline.{Annotation, StanfordCoreNLP}
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations
import edu.stanford.nlp.trees.TreeCoreAnnotations

import scala.collection.JavaConversions._

class StanfordChineseNlpExample {

  def runChineseAnnotators(text:String):Unit={
    val document=new Annotation(text)
    val pipeline=new StanfordCoreNLP("StanfordCoreNLP-chinese.properties")
    pipeline.annotate(document)
    val sentences=document.get(classOf[CoreAnnotations.SentencesAnnotation])
    for(sentence<-sentences){
      for(token<-sentence.get(classOf[CoreAnnotations.TokensAnnotation])){
        val word=token.get(classOf[CoreAnnotations.TextAnnotation])
        val pos=token.get(classOf[CoreAnnotations.PartOfSpeechAnnotation])
        val ne=token.get(classOf[CoreAnnotations.NamedEntityTagAnnotation])
        println(word+"--"+pos+"--"+ne)
      }
      val tree=sentence.get(classOf[TreeCoreAnnotations.TreeAnnotation])
      println("语法树:")
      println(tree.toString)
      val dependencies=sentence.get(classOf[SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation])
      println("依存句法:")
      println(dependencies.toString())
    }
  }
}

object StanfordChineseNlpExample{
  def main(args: Array[String]): Unit = {
    val example=new StanfordChineseNlpExample()
    example.runChineseAnnotators(
      """
        |克林顿说，华盛顿将逐步落实对韩国的经济援助。
        |金大中对克林顿的讲话报以掌声：克林顿总统在会谈中重申，他坚定地支持韩国摆脱经济危机
      """.stripMargin)
  }
}
